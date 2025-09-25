package com.ecomweb.identity_service.configuration;


import com.ecomweb.identity_service.constant.PredefinedRole;
import com.ecomweb.identity_service.entity.Role;
import com.ecomweb.identity_service.entity.User;
import com.ecomweb.identity_service.entity.UserRole;
import com.ecomweb.identity_service.repository.RoleRepository;
import com.ecomweb.identity_service.repository.UserRepository;
import com.ecomweb.identity_service.repository.UserRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository  userRoleRepository) {
        log.info("Initializing application.....");
        return args -> {
            if (userRepository.findByUsernameAndActive(ADMIN_USER_NAME,true).isEmpty()) {
                roleRepository.save(Role.builder()
                        .id(PredefinedRole.USER_ROLE)
                        .description("User role")
                        .build());

                roleRepository.save(Role.builder()
                        .id(PredefinedRole.SELLER_ROLE)
                        .description("Seller role")
                        .build());

                Role adminRole = roleRepository.save(Role.builder()
                        .id(PredefinedRole.ADMIN_ROLE)
                        .description("Admin role")
                        .build());


                User user = User.builder()
                        .active(true)
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .build();

                user = userRepository.save(user);

                UserRole userRole = UserRole.builder()
                        .roleId(adminRole.getId())
                        .userId(user.getId())
                        .build();
                userRoleRepository.save(userRole);

                log.warn("admin user has been created with default password: admin, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }
}
