package com.tplite.core_banking.module.account.dto;

import java.math.BigDecimal;

import com.tplite.core_banking.common.validation.EnumParser;
import com.tplite.core_banking.common.validation.ValueOfEnum;
import com.tplite.core_banking.module.account.entity.AccountType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateAccountRequest {
    @NotBlank(message = "Account type is required")
    @ValueOfEnum(enumClass = AccountType.class, message = "Account type is invalid")
    private String accountType;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters")
    private String currency = "VND";

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.00", message = "Initial balance must be greater than or equal to 0")
    private BigDecimal initialBalance = BigDecimal.ZERO;

    public CreateAccountRequest() {
    }

    public AccountType getAccountType() {
        return EnumParser.parse(AccountType.class, accountType);
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
