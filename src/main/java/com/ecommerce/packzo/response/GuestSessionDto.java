package com.ecommerce.packzo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestSessionDto {

    private String guestToken;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private boolean active;
}