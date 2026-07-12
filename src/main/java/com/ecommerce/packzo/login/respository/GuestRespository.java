package com.ecommerce.packzo.login.respository;

import com.ecommerce.packzo.entity.GuestSession;
import com.ecommerce.packzo.login.constants.GuestSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestRespository extends JpaRepository<GuestSession, String> {

    Optional<GuestSession> findByGuestTokenAndActiveTrue(
            String guestToken);

    Optional<GuestSession> findByGuestTokenAndStatus(
            String guestToken,
            GuestSessionStatus status);
}
