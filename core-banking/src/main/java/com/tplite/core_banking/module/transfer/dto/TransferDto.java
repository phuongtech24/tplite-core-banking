package com.tplite.core_banking.module.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.tplite.core_banking.common.validation.EnumParser;
import com.tplite.core_banking.common.validation.ValueOfEnum;
import com.tplite.core_banking.module.transfer.entity.Transaction;
import com.tplite.core_banking.module.transfer.entity.TransactionStatus;
import com.tplite.core_banking.module.transfer.entity.TransactionType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TransferDto {
    private UUID transactionId;
    private String transactionCode;

    @Size(max = 100, message = "Idempotency key must not exceed 100 characters")
    private String idempotencyKey;

    @NotNull(message = "From account is required")
    private UUID fromAccountId;

    @NotNull(message = "To account is required")
    private UUID toAccountId;

    private String fromAccountNumber;
    private String toAccountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be greater than or equal to 1")
    private BigDecimal amount;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters")
    private String currency;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @ValueOfEnum(enumClass = TransactionType.class, message = "Transaction type is invalid")
    private String type;

    @ValueOfEnum(enumClass = TransactionStatus.class, message = "Transaction status is invalid")
    private String status;

    private LocalDateTime createdAt;

    public TransferDto() {
    }

    public static TransferDto fromEntity(Transaction transaction) {
        TransferDto dto = new TransferDto();
        dto.setTransactionId(transaction.getId());
        dto.setTransactionCode(transaction.getTransactionCode());
        dto.setIdempotencyKey(transaction.getIdempotencyKey());
        dto.setFromAccountId(transaction.getFromAccount().getId());
        dto.setToAccountId(transaction.getToAccount().getId());
        dto.setFromAccountNumber(transaction.getFromAccount().getAccountNumber());
        dto.setToAccountNumber(transaction.getToAccount().getAccountNumber());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setDescription(transaction.getDescription());
        dto.setType(transaction.getType());
        dto.setStatus(transaction.getStatus());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    @AssertTrue(message = "From account and to account must be different")
    public boolean isDifferentAccounts() {
        return fromAccountId == null || toAccountId == null || !fromAccountId.equals(toAccountId);
    }

    public UUID getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(UUID fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public UUID getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(UUID toAccountId) {
        this.toAccountId = toAccountId;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionType getType() {
        return EnumParser.parse(TransactionType.class, type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setType(TransactionType type) {
        this.type = type == null ? null : type.name();
    }

    public TransactionStatus getStatus() {
        return EnumParser.parse(TransactionStatus.class, status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status == null ? null : status.name();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
