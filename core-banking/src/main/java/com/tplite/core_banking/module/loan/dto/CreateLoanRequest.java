package com.tplite.core_banking.module.loan.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateLoanRequest {
    @NotNull(message = "Loan product id is required")
    private UUID loanProductId;

    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "1.00", message = "Principal amount must be greater than or equal to 1")
    private BigDecimal principalAmount;

    @NotNull(message = "Term months is required")
    @Min(value = 1, message = "Term months must be at least 1")
    private Integer termMonths;

    public UUID getLoanProductId() { return loanProductId; }
    public void setLoanProductId(UUID loanProductId) { this.loanProductId = loanProductId; }
    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }
    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
}
