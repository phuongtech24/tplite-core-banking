package com.tplite.core_banking.module.account.dto;

import com.tplite.core_banking.module.account.entity.AccountStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateAccountStatusRequest {
    @NotNull(message = "Status is required")
    private AccountStatus status;

    public UpdateAccountStatusRequest() {
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
