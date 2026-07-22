package com.tplite.core_banking.module.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {
    private UUID userId;
    private String email;
    private String fullName;
    private Set<String> roles;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresInSeconds;

    public AuthResponse(UUID userId, String email, String fullName, Set<String> roles) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }

    public AuthResponse(
            UUID userId,
            String email,
            String fullName,
            Set<String> roles,
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresInSeconds
    ) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresInSeconds = expiresInSeconds;
    }
}
