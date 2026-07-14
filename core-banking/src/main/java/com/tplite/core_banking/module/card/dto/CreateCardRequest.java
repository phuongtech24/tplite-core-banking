package com.tplite.core_banking.module.card.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.tplite.core_banking.module.card.entity.CardType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class CreateCardRequest {
    @NotNull(message = "Account id is required")
    private UUID accountId;

    @NotNull(message = "Card type is required")
    private CardType cardType;

    @DecimalMin(value = "0.00", message = "Daily limit must be greater than or equal to 0")
    private BigDecimal dailyLimit = new BigDecimal("5000000.00");

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }
}
