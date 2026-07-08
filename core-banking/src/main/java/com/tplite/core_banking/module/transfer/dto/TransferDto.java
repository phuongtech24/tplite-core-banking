package com.tplite.core_banking.module.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.tplite.core_banking.module.transfer.entity.Transaction;
import com.tplite.core_banking.module.transfer.entity.TransactionStatus;
import com.tplite.core_banking.module.transfer.entity.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class TransferDto {

    private UUID transactionId;

    @NotNull(message = "Tài khoản chuyển không được để trống")
    private UUID fromAccountId;

    @NotNull(message = "Tài khoản nhận không được để trống")
    private UUID toAccountId;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "1.0", message = "Số tiền chuyển phải lớn hơn 0")
    private BigDecimal amount;

    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime createdAt;

    public TransferDto() {}

    public static TransferDto fromEntity(Transaction transaction) {
        TransferDto dto = new TransferDto();
        dto.setTransactionId(transaction.getId());
        dto.setFromAccountId(transaction.getFromAccount().getId());
        dto.setToAccountId(transaction.getToAccount().getId());
        dto.setAmount(transaction.getAmount());
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
