package com.ecommerce.packzo.entity;

import java.time.LocalDateTime;

import com.ecommerce.packzo.login.constants.CartStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "cart",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_cart_guest_variant",
            columnNames = {"guest_token", "product_variant_id"}
        ),
        @UniqueConstraint(
            name = "uk_cart_user_variant",
            columnNames = {"user_id", "product_variant_id"}
        )
    }, schema = "packzodev"
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "guest_token")
    private String guestToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "is_selected", nullable = false)
    @Builder.Default
    private Boolean isSelected = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name="status")
    private CartStatus status;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.quantity == null) {
            this.quantity = 1;
        }

        if (this.isSelected == null) {
            this.isSelected = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}