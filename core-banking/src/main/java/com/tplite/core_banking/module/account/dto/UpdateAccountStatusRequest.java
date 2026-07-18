package com.tplite.core_banking.module.account.dto;

import com.tplite.core_banking.common.validation.EnumParser;
import com.tplite.core_banking.common.validation.ValueOfEnum;
import com.tplite.core_banking.module.account.entity.AccountStatus;

import jakarta.validation.constraints.NotBlank;

public class UpdateAccountStatusRequest {
    @NotBlank(message = "Status is required")
    @ValueOfEnum(enumClass = AccountStatus.class, message = "Status is invalid")
    private String status;

    public UpdateAccountStatusRequest() {
    }

    public AccountStatus getStatus() {
        return EnumParser.parse(AccountStatus.class, status);
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
