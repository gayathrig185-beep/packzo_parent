package com.ecommerce.packzo.login.service;

import com.ecommerce.packzo.login.entity.User;
import com.ecommerce.packzo.login.respository.UserRepository;
import com.ecommerce.packzo.login.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements  UserDetailsService{

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);

    private final UserRepository userRepository;

    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        logger.debug("Loading user : {}", email);

        User user = userRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> {

                    logger.warn("User not found : {}", email);

                    return new UsernameNotFoundException(
                            "Invalid username or password");
                });

        if (!Boolean.TRUE.equals(user.getActive())) {

            logger.warn("Inactive user : {}", email);

            throw new UsernameNotFoundException(
                    "User account is inactive");
        }

        return new CustomUserDetails(user);
    }

}