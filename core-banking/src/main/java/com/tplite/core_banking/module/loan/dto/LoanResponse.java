package com.tplite.core_banking.module.loan.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.tplite.core_banking.module.loan.entity.Loan;
import com.tplite.core_banking.module.loan.entity.LoanStatus;

public class LoanResponse {
    private UUID id;
    private String loanCode;
    private UUID customerId;
    private String customerName;
    private UUID loanProductId;
    private String loanProductName;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer termMonths;
    private BigDecimal outstandingBalance;
    private BigDecimal estimatedTotalInterest;
    private BigDecimal estimatedTotalPayable;
    private String interestStrategy;
    private LoanStatus status;
    private UUID approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LoanResponse from(Loan loan, BigDecimal totalInterest, String interestStrategy) {
        LoanResponse response = new LoanResponse();
        response.setId(loan.getId());
        response.setLoanCode(loan.getLoanCode());
        response.setCustomerId(loan.getCustomer().getId());
        response.setCustomerName(loan.getCustomer().getFullName());
        response.setLoanProductId(loan.getLoanProduct().getId());
        response.setLoanProductName(loan.getLoanProduct().getName());
        response.setPrincipalAmount(loan.getPrincipalAmount());
        response.setInterestRate(loan.getInterestRate());
        response.setTermMonths(loan.getTermMonths());
        response.setOutstandingBalance(loan.getOutstandingBalance());
        response.setEstimatedTotalInterest(totalInterest);
        response.setEstimatedTotalPayable(loan.getPrincipalAmount().add(totalInterest));
        response.setInterestStrategy(interestStrategy);
        response.setStatus(loan.getStatus());
        response.setApprovedBy(loan.getApprovedBy() == null ? null : loan.getApprovedBy().getId());
        response.setApprovedAt(loan.getApprovedAt());
        response.setCreatedAt(loan.getCreatedAt());
        response.setUpdatedAt(loan.getUpdatedAt());
        return response;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getLoanCode() { return loanCode; }
    public void setLoanCode(String loanCode) { this.loanCode = loanCode; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public UUID getLoanProductId() { return loanProductId; }
    public void setLoanProductId(UUID loanProductId) { this.loanProductId = loanProductId; }
    public String getLoanProductName() { return loanProductName; }
    public void setLoanProductName(String loanProductName) { this.loanProductName = loanProductName; }
    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
    public BigDecimal getOutstandingBalance() { return outstandingBalance; }
    public void setOutstandingBalance(BigDecimal outstandingBalance) { this.outstandingBalance = outstandingBalance; }
    public BigDecimal getEstimatedTotalInterest() { return estimatedTotalInterest; }
    public void setEstimatedTotalInterest(BigDecimal estimatedTotalInterest) { this.estimatedTotalInterest = estimatedTotalInterest; }
    public BigDecimal getEstimatedTotalPayable() { return estimatedTotalPayable; }
    public void setEstimatedTotalPayable(BigDecimal estimatedTotalPayable) { this.estimatedTotalPayable = estimatedTotalPayable; }
    public String getInterestStrategy() { return interestStrategy; }
    public void setInterestStrategy(String interestStrategy) { this.interestStrategy = interestStrategy; }
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
    public UUID getApprovedBy() { return approvedBy; }
    public void setApprovedBy(UUID approvedBy) { this.approvedBy = approvedBy; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
