package com.qhuyns.ecomweb.repository;


import com.qhuyns.ecomweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    Optional<User> findByUsernameAndActive(String username, Boolean active);
    Optional<User> findByIdAndActive(String id, Boolean active);
}
