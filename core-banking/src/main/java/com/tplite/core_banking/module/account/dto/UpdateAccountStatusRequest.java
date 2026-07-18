package com.tplite.core_banking.module.account.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.tplite.core_banking.common.validation.EnumParser;
import com.tplite.core_banking.common.validation.ValueOfEnum;
import com.tplite.core_banking.module.account.entity.AccountStatus;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class UpdateAccountStatusRequest {
    @NotBlank(message = "Status is required")
    @ValueOfEnum(enumClass = AccountStatus.class, message = "Status is invalid")
    private String status;

    public AccountStatus getStatus() {
        return EnumParser.parse(AccountStatus.class, status);
    }
}
