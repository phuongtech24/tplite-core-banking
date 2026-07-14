package com.tplite.core_banking.module.user.dto;

import com.tplite.core_banking.module.user.entity.UserStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateUserStatusRequest {
    @NotNull(message = "Status is required")
    private UserStatus status;

    public UpdateUserStatusRequest() {
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
