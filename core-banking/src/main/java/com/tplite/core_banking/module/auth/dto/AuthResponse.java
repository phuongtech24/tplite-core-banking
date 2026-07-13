package com.tplite.core_banking.module.auth.dto;

import java.util.Set;
import java.util.UUID;

public class AuthResponse {
    private UUID userId;
    private String email;
    private String fullName;
    private Set<String> roles;

    public AuthResponse() {
    }

    public AuthResponse(UUID userId, String email, String fullName, Set<String> roles) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
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
}
