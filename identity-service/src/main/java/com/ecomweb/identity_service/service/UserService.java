package com.ecomweb.identity_service.service;


import com.ecomweb.identity_service.constant.PredefinedRole;
import com.ecomweb.identity_service.dto.event.EmailVerificationRequest;
import com.ecomweb.identity_service.dto.event.UserCreated;
import com.ecomweb.identity_service.dto.event.UserSnapshot;
import com.ecomweb.identity_service.dto.request.UpgradeSellerRequest;
import com.ecomweb.identity_service.dto.request.UserCreationRequest;
import com.ecomweb.identity_service.dto.request.UserUpdateRequest;
import com.ecomweb.identity_service.dto.response.UserResponse;
import com.ecomweb.identity_service.entity.Role;
import com.ecomweb.identity_service.entity.User;
import com.ecomweb.identity_service.entity.UserRole;
import com.ecomweb.identity_service.entity.VerificationToken;
import com.ecomweb.identity_service.exception.AppException;
import com.ecomweb.identity_service.exception.ErrorCode;
import com.ecomweb.identity_service.mapper.UserMapper;
import com.ecomweb.identity_service.producer.UserProducer;
import com.ecomweb.identity_service.repository.RoleRepository;
import com.ecomweb.identity_service.repository.UserRepository;
import com.ecomweb.identity_service.repository.UserRoleRepository;
import com.ecomweb.identity_service.repository.VerificationTokenRepository;
import com.ecomweb.identity_service.util.RedisCacheHelper;
import com.ecomweb.identity_service.util.RedisKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
// Nếu đã có Session sẵn (ví dụ do OSIV mở ở đầu request): @Transactional sẽ dùng lại Session hiện tại.


// chi rollback duoc nhung loi tra ra la runexception va cac thao tac cung 1 database, (nhiều db -> gọi service khác, dung saga)
// neu A goi B (db rieng nhung cung service server) thi neu A fail ma B da commit thi chi rollback dc A thoi -> su dung saga de handle
public class UserService {

    // các nghiệp vụ chính của service thì phải dùng rest để call để đảm bảo response chính xsc success hay fail
    // các nghiệp vụ phụ của service thì mới nên dùng message broker
    // thường các nghiệp vụ xử lí chính hay compensation của MB thường nằm gói gọn trong  service đó nên thường sẽ k phải viết compen của nó mà sẽ tự rollback
    UserRepository userRepository;
    UserMapper userMapper;
    UserRoleRepository userRoleRepository;
    RoleRepository roleRepository;
    RedisCacheHelper cacheHelper;
    VerificationTokenRepository  verificationTokenRepository;
    PasswordEncoder passwordEncoder;
    UserProducer userProducer;


    @NonFinal
    @Value("${spring.mail.expiryTime}")
    int expiryTime;
    @Transactional
    public UserResponse createUser(UserCreationRequest request) throws Exception {
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(false);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        //tao token verification
        VerificationToken vt = VerificationToken.builder()
                .userId(user.getId())
                .expiryDate(LocalDateTime.now().plusSeconds(expiryTime))
                .token(UUID.randomUUID().toString())
                .build();
        // set role sau khi da co userId
        Role role = roleRepository.findById(PredefinedRole.USER_ROLE).orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_EXISTS));
        UserRole userRole = UserRole.builder()
                .userId(user.getId())
                .roleId(role.getName())
                .build();
        userRoleRepository.save(userRole);
        userProducer.userCreated(UserCreated.builder()
                        .emailVerificationRequest(EmailVerificationRequest.builder()
                                .email(user.getEmail())
                                .token(vt.getToken())
                                .username(user.getUsername())
                                .build())
                .build());
        return userMapper.toUserResponse(user);
    }

    public String getUserIdByUsername(String username){
        User user = userRepository.findByUsernameAndActive(
                username,true
        ).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        return user.getId();
    }

    @Transactional
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
        userRoleRepository.deleteByUserId(user.getId());
        Role role = roleRepository.findById(PredefinedRole.SELLER_ROLE).orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_EXISTS));
        UserRole userRole = UserRole.builder()
                .userId(user.getId())
                .roleId(role.getName())
                .build();
        userRoleRepository.save(userRole);
        userRepository.save(user);

        return user.getId();
    }

    @Transactional
    public void upgradeSellerRollback(UserSnapshot data) {
    // khong xu li compensation cua compensation
    // chi xu li khi compen thay doi tren nhieu db khac nhau bi fail -> saga handle
        User user = userRepository.findByIdAndActive(
                data.getId(),true
        ).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setFullName(data.getFullName());
        userRoleRepository.deleteByUserId(user.getId());
        Role role = roleRepository.findById(PredefinedRole.USER_ROLE).orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_EXISTS));
        UserRole userRole = UserRole.builder()
                .userId(user.getId())
                .roleId(role.getName())
                .build();
        userRoleRepository.save(userRole);
        userRepository.save(user);
    }

    @Transactional
    public void activeUser(String username,String token) {
        User user = userRepository.findByUsernameAndActive(username,false)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        List<VerificationToken> verificationTokens = verificationTokenRepository.findByUserId(user.getId());
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


    @Transactional
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
            for(String roleId : request.getRoles()){
                Role role = roleRepository.findById(roleId).orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_EXISTS));
                UserRole userRole = UserRole.builder()
                        .userId(user.getId())
                        .roleId(role.getName())
                        .build();
                userRoleRepository.save(userRole);
            }
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    public void deleteUser(String userId) {
//        userRepository.deleteById(userId);
//    }
//
//
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }
}

