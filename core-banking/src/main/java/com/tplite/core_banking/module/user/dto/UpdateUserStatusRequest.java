package com.tplite.core_banking.module.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.tplite.core_banking.common.validation.EnumParser;
import com.tplite.core_banking.common.validation.ValueOfEnum;
import com.tplite.core_banking.module.user.entity.UserStatus;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserStatusRequest {
    @NotBlank(message = "Status is required")
    @ValueOfEnum(enumClass = UserStatus.class, message = "Status is invalid")
    private String status;

    public UserStatus getStatus() {
        return EnumParser.parse(UserStatus.class, status);
    }
}
