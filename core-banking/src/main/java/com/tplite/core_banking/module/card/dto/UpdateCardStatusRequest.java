package com.tplite.core_banking.module.card.dto;

import com.tplite.core_banking.module.card.entity.CardStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateCardStatusRequest {
    @NotNull(message = "Status is required")
    private CardStatus status;

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }
}
