package com.ecommerce.packzo.login.respository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ecommerce.packzo.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserUserId(String userId);

    void deleteByUserUserId(String userId);

    List<RefreshToken> findByUserUserIdAndRevokedFalse(String userId);

    long deleteByExpiresAtBefore(LocalDateTime dateTime);

}