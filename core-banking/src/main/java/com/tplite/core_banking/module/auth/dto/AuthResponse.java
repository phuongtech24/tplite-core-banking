package com.tplite.core_banking.module.auth.dto;

import java.util.Set;
import java.util.UUID;

public class AuthResponse {
    private UUID userId;
    private String email;
    private String fullName;
    private Set<String> roles;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresInSeconds;

    public AuthResponse() {
    }

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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }
}
