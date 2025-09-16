package com.qhuyns.ecomweb.service;


import com.qhuyns.ecomweb.constant.PredefinedRole;
import com.qhuyns.ecomweb.dto.event.UserSnapshot;
import com.qhuyns.ecomweb.dto.request.UpgradeSellerRequest;
import com.qhuyns.ecomweb.dto.request.UserCreationRequest;
import com.qhuyns.ecomweb.dto.request.UserUpdateRequest;
import com.qhuyns.ecomweb.dto.response.UserResponse;
import com.qhuyns.ecomweb.entity.Role;
import com.qhuyns.ecomweb.entity.User;
import com.qhuyns.ecomweb.entity.VerificationToken;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.UserMapper;
import com.qhuyns.ecomweb.repository.RoleRepository;
import com.qhuyns.ecomweb.repository.UserRepository;
import com.qhuyns.ecomweb.util.RedisCacheHelper;
import com.qhuyns.ecomweb.util.RedisKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
// transactional : rollback va giu session
// 1 session chinh la 1 entity manager, dai dien cho 1 phien lam viec voi db, quan ly voi hibernate
// cac http request duoc tao va giu session nho co che OSIV, session duco giu cho toi khi co response
// cac service duoc thuc thi boi message broker khong di qua request -> chi mo session theo 1 truy van
// khi do neu entity co list lazy thi khong the get duoc ra nua vi session se dong ngay sau khi querry(ss gan voi qr) -> k co entity manager de truy van nua
// phai gan Transactional de giu session gan voi method do thi session se k dong ngay sau khi querry xong
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    EmailService emailService;
    RedisCacheHelper cacheHelper;

    @NonFinal
    @Value("${spring.mail.expiryTime}")
    int expiryTime;
    public UserResponse createUser(UserCreationRequest request) throws Exception {
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(false);

        List<Role> roles = new ArrayList<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);
        VerificationToken vt = VerificationToken.builder()
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(expiryTime))
                .token(UUID.randomUUID().toString())
                .build();
        user.getVerificationTokens().add(vt);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        emailService.sendVerificationEmail(user.getEmail(), vt.getToken(),user.getUsername());
        return userMapper.toUserResponse(user);
    }

    public String getUserIdByUsername(String username){
        User user = userRepository.findByUsernameAndActive(
                username,true
        ).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        return user.getId();
    }

    public String upgradeSellerRequest(UpgradeSellerRequest request) throws Exception {
       User user = userRepository.findByUsernameAndActive(
               SecurityContextHolder.getContext().getAuthentication().getName(),true
       ).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));

        cacheHelper.saveToCache(RedisKey.ROLLBACK_TO_SELLER.getKey()+user.getId()
                , UserSnapshot.builder()
                                .id(user.getId())
                        .fullName(user.getFullName())
                        .build()
                , RedisKey.ROLLBACK_TO_SELLER.getTtl());

       user.setFullName(request.getShopName());
       user.getRoles().clear();
       user.getRoles().add(roleRepository.findById(PredefinedRole.USER_ROLE)
               .orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_EXISTS)));
       userRepository.save(user);


       return user.getId();
    }

    @Transactional
    public void upgradeSellerRollback(UserSnapshot data) {

        User user = userRepository.findByIdAndActive(
                data.getId(),true
        ).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setFullName(data.getFullName());
        user.getRoles().clear();
        user.getRoles().add(roleRepository.findById(PredefinedRole.USER_ROLE)
                .orElseThrow(()-> new  AppException(ErrorCode.ROLE_NOT_EXISTS)));
        userRepository.save(user);
    }

    public void activeUser(String username,String token) {
        User user = userRepository.findByUsernameAndActive(username,false)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        List<VerificationToken> verificationTokens = user.getVerificationTokens();
        for (VerificationToken verificationToken : verificationTokens) {
            if (verificationToken.getToken().equals(token)) {
                if(verificationToken.getExpiryDate().isAfter(LocalDateTime.now())) {
                    user.setActive(true);
                    userRepository.save(user);
                    return;
                }
                else{
                    throw new AppException(ErrorCode.VERIFICATION_TOKEN_IS_EXPRIRED);
                }
            }
        }
        throw new AppException(ErrorCode.VERIFICATION_TOKEN_IS_EXPRIRED);

    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsernameAndActive(name,true).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }


//    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse updateUser( UserUpdateRequest request) {

        User user = userRepository.findByUsernameAndActive(SecurityContextHolder.getContext().getAuthentication().getName(),true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        if(request.getPassword()!=null && !request.getPassword().equals(request.getRepeatPassword())){
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        if(request.getPassword()!=null){
            if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            } else {
                throw new AppException(ErrorCode.WRONG_PASSWORD);
            }
        }

        if(request.getRoles()!=null){
            var roles = roleRepository.findAllById(request.getRoles());
            user.setRoles(new ArrayList<>(roles));
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }


    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }
}
