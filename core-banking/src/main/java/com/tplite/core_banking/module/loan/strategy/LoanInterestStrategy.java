package com.tplite.core_banking.module.loan.strategy;

import java.math.BigDecimal;

public interface LoanInterestStrategy {
    String strategyName();

    BigDecimal calculateTotalInterest(BigDecimal principalAmount, BigDecimal annualInterestRate, Integer termMonths);
}
