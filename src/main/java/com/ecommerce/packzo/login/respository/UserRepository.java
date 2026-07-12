package com.ecommerce.packzo.login.respository;

import java.util.Optional;

import com.ecommerce.packzo.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByMobile(String mobile);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMobile(String mobile);

}