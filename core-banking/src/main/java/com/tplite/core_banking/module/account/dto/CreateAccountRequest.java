package com.tplite.core_banking.module.account.dto;

import java.math.BigDecimal;

import com.tplite.core_banking.module.account.entity.AccountType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateAccountRequest {
    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters")
    private String currency = "VND";

    @DecimalMin(value = "0.00", message = "Initial balance must be greater than or equal to 0")
    private BigDecimal initialBalance = BigDecimal.ZERO;

    public CreateAccountRequest() {
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
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
