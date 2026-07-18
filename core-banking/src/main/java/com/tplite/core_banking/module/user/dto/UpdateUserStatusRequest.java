package com.tplite.core_banking.module.user.dto;

import com.tplite.core_banking.common.validation.EnumParser;
import com.tplite.core_banking.common.validation.ValueOfEnum;
import com.tplite.core_banking.module.user.entity.UserStatus;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserStatusRequest {
    @NotBlank(message = "Status is required")
    @ValueOfEnum(enumClass = UserStatus.class, message = "Status is invalid")
    private String status;

    public UpdateUserStatusRequest() {
    }

    public UserStatus getStatus() {
        return EnumParser.parse(UserStatus.class, status);
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
