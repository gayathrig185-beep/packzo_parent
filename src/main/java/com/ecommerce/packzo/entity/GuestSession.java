package com.ecommerce.packzo.entity;

import com.ecommerce.packzo.login.constants.GuestSessionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="guest_session", schema = "packzodev")
public class GuestSession {

    @Id
    private String guestToken;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private Boolean active;

    private GuestSessionStatus status;

    private LocalDateTime deactivatedAt;

    private String deactivationReason;




}