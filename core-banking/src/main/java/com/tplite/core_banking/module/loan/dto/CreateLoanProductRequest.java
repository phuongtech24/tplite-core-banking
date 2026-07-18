package com.tplite.core_banking.module.loan.dto;

import jakarta.validation.constraints.AssertTrue;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateLoanProductRequest {
    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.01", message = "Interest rate must be greater than 0")
    private BigDecimal interestRate;

    @NotNull(message = "Min amount is required")
    @DecimalMin(value = "0.00", message = "Min amount must be greater than or equal to 0")
    private BigDecimal minAmount;

    @NotNull(message = "Max amount is required")
    @DecimalMin(value = "0.01", message = "Max amount must be greater than 0")
    private BigDecimal maxAmount;

    @NotNull(message = "Min term months is required")
    @Min(value = 1, message = "Min term months must be at least 1")
    private Integer minTermMonths;

    @NotNull(message = "Max term months is required")
    @Min(value = 1, message = "Max term months must be at least 1")
    private Integer maxTermMonths;
    @AssertTrue(message = "Max amount must be greater than or equal to min amount")
    public boolean isAmountRangeValid() {
        return minAmount == null || maxAmount == null || maxAmount.compareTo(minAmount) >= 0;
    }

    @AssertTrue(message = "Max term months must be greater than or equal to min term months")
    public boolean isTermRangeValid() {
        return minTermMonths == null || maxTermMonths == null || maxTermMonths >= minTermMonths;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public BigDecimal getMinAmount() { return minAmount; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    public BigDecimal getMaxAmount() { return maxAmount; }
    public void setMaxAmount(BigDecimal maxAmount) { this.maxAmount = maxAmount; }
    public Integer getMinTermMonths() { return minTermMonths; }
    public void setMinTermMonths(Integer minTermMonths) { this.minTermMonths = minTermMonths; }
    public Integer getMaxTermMonths() { return maxTermMonths; }
    public void setMaxTermMonths(Integer maxTermMonths) { this.maxTermMonths = maxTermMonths; }
}
