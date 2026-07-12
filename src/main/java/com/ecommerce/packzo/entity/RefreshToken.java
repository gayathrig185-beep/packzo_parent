package com.ecommerce.packzo.entity;

import java.time.LocalDateTime;

import com.ecommerce.packzo.login.entity.BaseAuditEntity;
import com.ecommerce.packzo.login.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(
        name = "refresh_token",
        indexes = {
                @Index(name = "idx_refresh_user", columnList = "user_id"),
                @Index(name = "idx_refresh_token", columnList = "token"),
                @Index(name = "idx_refresh_expiry", columnList = "expires_at")
        }
)
public class RefreshToken extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            nullable = false)
    private User user;

    @Column(name = "device_id", length = 150)
    private String deviceId;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean revoked = false;

    // Getters and Setters
}