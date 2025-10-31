package com.ecomweb.identity_service.service;


import com.ecomweb.identity_service.constant.PredefinedRole;
import com.ecomweb.identity_service.dto.event.EmailVerificationRequest;
import com.ecomweb.identity_service.dto.event.UserCreated;
import com.ecomweb.identity_service.dto.event.UserSnapshot;
import com.ecomweb.identity_service.dto.request.UpgradeSellerRequest;
import com.ecomweb.identity_service.dto.request.UserCreationRequest;
import com.ecomweb.identity_service.dto.request.UserUpdateRequest;
import com.ecomweb.identity_service.dto.response.RoleResponse;
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
import com.ecomweb.identity_service.util.JwtUtil;
import com.ecomweb.identity_service.util.RedisCacheHelper;
import com.ecomweb.identity_service.util.RedisKey;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
    Keycloak keycloak;


    @NonFinal
    @Value("${spring.mail.expiryTime}")
    int expiryTime;

    @Value("${keycloak.realm}")
    @NonFinal
    private String realm;

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
                .roleId(role.getId())
                .build();
        userRoleRepository.save(userRole);
        verificationTokenRepository.save(vt);
        userProducer.userCreated(UserCreated.builder()
                        .emailVerificationRequest(EmailVerificationRequest.builder()
                                .email(user.getEmail())
                                .token(vt.getToken())
                                .username(user.getUsername())
                                .build())
                .build());
        return userMapper.toUserResponse(user);
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
                .roleId(role.getId())
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
                .roleId(role.getId())
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
        UserResponse userResponse = userMapper.toUserResponse(user);
        List<RoleResponse> roles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(ur -> RoleResponse.builder()
                        .id(ur.getRoleId())
                        .build()).collect(Collectors.toList());
        userResponse.setRoles(roles);
        return userResponse;
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
                        .roleId(role.getId())
                        .build();
                userRoleRepository.save(userRole);
            }
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser2( UserUpdateRequest request) {
        String un = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsernameAndActive(un,true)
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
            userRoleRepository.deleteByUserId(user.getId());
            for(String roleId : request.getRoles()){
                Role role = roleRepository.findById(roleId).orElseThrow(()->new AppException(ErrorCode.ROLE_NOT_EXISTS));
                UserRole userRole = UserRole.builder()
                        .userId(user.getId())
                        .roleId(role.getId())
                        .build();
                userRoleRepository.save(userRole);
            }
        }
        try {
            // Lấy user resource từ Keycloak bằng Keycloak ID
            UserResource userResource = keycloak
                    .realm(realm)
                    .users()
                    .get(user.getKeycloakId());

            // Lấy thông tin hiện tại trên Keycloak
            UserRepresentation userRep = userResource.toRepresentation();

            if (request.getPassword() != null) {
                CredentialRepresentation cred = new CredentialRepresentation();
                cred.setType(CredentialRepresentation.PASSWORD);
                cred.setValue(request.getPassword());
                cred.setTemporary(false);
                userResource.resetPassword(cred);
            }

            if (request.getRoles() != null && !request.getRoles().isEmpty()) {
                RealmResource realmResource = keycloak.realm(realm);

                List<RoleRepresentation> oldRoles = realmResource.users()
                        .get(user.getKeycloakId())
                        .roles()
                        .realmLevel()
                        .listAll();
                if (!oldRoles.isEmpty()) {
                    userResource.roles().realmLevel().remove(oldRoles);
                }

                List<RoleRepresentation> newRoles = new ArrayList<>();
                for (String roleName : request.getRoles()) {
                    RoleRepresentation roleRep = realmResource.roles().get(roleName).toRepresentation();
                    newRoles.add(roleRep);
                }
                userResource.roles().realmLevel().add(newRoles);
            }


        } catch (WebApplicationException ex) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
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

    public UserResponse getActivatedUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findByIdAndActive(id,true).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public UserResponse getActivatedUserByUsername(String username) {
        return userMapper.toUserResponse(
                userRepository.findByUsernameAndActive(username,true).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public boolean existById(String id) {
        return userRepository.existsById(id);
    }

    public String getUserIdByUsername(String username) {
        return userRepository.findByUsernameAndActive(username,true)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED))
                .getId();
    }




    @Transactional
    public void createUser2(UserCreationRequest request) throws Exception {
        // 1. Validate password
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }


        String userKeycloakId="";
        try {
            // 2. Tạo user representation cho Keycloak
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setUsername(request.getUsername());
//            userRepresentation.setEmail(request.getEmail());
            userRepresentation.setEnabled(false);
//            userRepresentation.setEmailVerified(false);

            // 3. Set credentials
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());
            credential.setTemporary(false);
            userRepresentation.setCredentials(Arrays.asList(credential));

            // 4. Tạo user trong Keycloak
            Response response = keycloak.realm(realm).users().create(userRepresentation);

            if (response.getStatus() != 201) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }

            // 5. Lấy ID của user mới tạo
            userKeycloakId = CreatedResponseUtil.getCreatedId(response);

            // 6. Gán role mặc định (USER)
            UserResource userResource = keycloak.realm(realm).users().get(userKeycloakId);
            RoleRepresentation userRole = keycloak.realm(realm)
                    .roles()
                    .get("USER")
                    .toRepresentation();

            userResource.roles().realmLevel().add(Arrays.asList(userRole));

            userResource.update(userRepresentation);



        } catch (WebApplicationException ex) {
            if (ex.getResponse().getStatus() == 409) {
                throw new AppException(ErrorCode.USER_EXISTED);
            }
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(false);
        user.setKeycloakId(userKeycloakId);

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
        UserRole userRole1 = UserRole.builder()
                .userId(user.getId())
                .roleId(role.getId())
                .build();
        userRoleRepository.save(userRole1);
        verificationTokenRepository.save(vt);
        userProducer.userCreated(UserCreated.builder()
                .emailVerificationRequest(EmailVerificationRequest.builder()
                        .email(user.getEmail())
                        .token(vt.getToken())
                        .username(user.getUsername())
                        .build())
                .build());
    }
    @Transactional
    public void activeUser2(String username,String token) {
        User user = userRepository.findByUsernameAndActive(username,false)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        List<VerificationToken> verificationTokens = verificationTokenRepository.findByUserId(user.getId());
        for (VerificationToken verificationToken : verificationTokens) {
            if (verificationToken.getToken().equals(token)) {
                if(verificationToken.getExpiryDate().isAfter(LocalDateTime.now())) {
                    user.setActive(true);
                    userRepository.save(user);

                    try {
                        // Lấy user resource từ Keycloak bằng ID
                        UserResource userResource = keycloak.
                                realm(realm).users().get(user.getKeycloakId());

                        // Lấy thông tin user (UserRepresentation)
                        UserRepresentation userRep = userResource.toRepresentation();

                        // Enable user và đánh dấu email đã verify
                        userRep.setEnabled(true);

                        // Xóa verification token

                        userResource.update(userRep);

                    } catch (WebApplicationException ex) {
                        throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
                    }

                    return;
                }
                else{
                    throw new AppException(ErrorCode.VERIFICATION_TOKEN_IS_EXPRIRED);
                }
            }
        }
        throw new AppException(ErrorCode.VERIFICATION_TOKEN_IS_EXPRIRED);



    }

    @Transactional
    public String upgradeSellerRequest2(UpgradeSellerRequest request) throws Exception {
        String un = SecurityContextHolder.getContext().getAuthentication().getName();
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
                .roleId(role.getId())
                .build();
        userRoleRepository.save(userRole);
        userRepository.save(user);

        try {
            UserResource userResource = keycloak
                    .realm(realm)
                    .users()
                    .get(user.getKeycloakId());

            RealmResource realmResource = keycloak.realm(realm);

            List<RoleRepresentation> oldRoles = realmResource.users()
                    .get(user.getKeycloakId())
                    .roles()
                    .realmLevel()
                    .listAll();
            if (!oldRoles.isEmpty()) {
                userResource.roles().realmLevel().remove(oldRoles);
            }

            List<RoleRepresentation> newRoles = new ArrayList<>();
            RoleRepresentation roleRep = realmResource.roles().get(PredefinedRole.SELLER_ROLE).toRepresentation();
            newRoles.add(roleRep);
            userResource.roles().realmLevel().add(newRoles);

        } catch (WebApplicationException ex) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        return user.getId();
    }

    @Transactional
    public void upgradeSellerRollback2(UserSnapshot data) {
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
                .roleId(role.getId())
                .build();
        userRoleRepository.save(userRole);
        userRepository.save(user);

        try {
            UserResource userResource = keycloak
                    .realm(realm)
                    .users()
                    .get(user.getKeycloakId());

            RealmResource realmResource = keycloak.realm(realm);

            List<RoleRepresentation> oldRoles = realmResource.users()
                    .get(user.getKeycloakId())
                    .roles()
                    .realmLevel()
                    .listAll();
            if (!oldRoles.isEmpty()) {
                userResource.roles().realmLevel().remove(oldRoles);
            }

            List<RoleRepresentation> newRoles = new ArrayList<>();
            RoleRepresentation roleRep = realmResource.roles().get(PredefinedRole.SELLER_ROLE).toRepresentation();
            newRoles.add(roleRep);
            userResource.roles().realmLevel().add(newRoles);

        } catch (WebApplicationException ex) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }


        }

