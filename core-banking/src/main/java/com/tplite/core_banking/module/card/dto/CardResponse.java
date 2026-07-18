package com.tplite.core_banking.module.card.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.tplite.core_banking.module.card.entity.Card;
import com.tplite.core_banking.module.card.entity.CardStatus;
import com.tplite.core_banking.module.card.entity.CardType;

@Getter
@Setter
@NoArgsConstructor
public class CardResponse {
    private UUID id;
    private UUID accountId;
    private String accountNumber;
    private String cardNumberMasked;
    private CardType cardType;
    private CardStatus status;
    private BigDecimal dailyLimit;
    private LocalDateTime issuedAt;
    private LocalDate expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CardResponse from(Card card) {
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setAccountId(card.getAccount().getId());
        response.setAccountNumber(card.getAccount().getAccountNumber());
        response.setCardNumberMasked(card.getCardNumberMasked());
        response.setCardType(card.getCardType());
        response.setStatus(card.getStatus());
        response.setDailyLimit(card.getDailyLimit());
        response.setIssuedAt(card.getIssuedAt());
        response.setExpiredAt(card.getExpiredAt());
        response.setCreatedAt(card.getCreatedAt());
        response.setUpdatedAt(card.getUpdatedAt());
        return response;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getCardNumberMasked() { return cardNumberMasked; }
    public void setCardNumberMasked(String cardNumberMasked) { this.cardNumberMasked = cardNumberMasked; }
    public CardType getCardType() { return cardType; }
    public void setCardType(CardType cardType) { this.cardType = cardType; }
    public CardStatus getStatus() { return status; }
    public void setStatus(CardStatus status) { this.status = status; }
    public BigDecimal getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(BigDecimal dailyLimit) { this.dailyLimit = dailyLimit; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    public LocalDate getExpiredAt() { return expiredAt; }
    public void setExpiredAt(LocalDate expiredAt) { this.expiredAt = expiredAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
