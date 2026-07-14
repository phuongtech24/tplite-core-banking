package com.tplite.core_banking.module.loan.strategy;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class LoanInterestCalculator {
    private final LoanInterestStrategy strategy;

    public LoanInterestCalculator(SimpleInterestStrategy strategy) {
        this.strategy = strategy;
    }

    public BigDecimal calculateTotalInterest(BigDecimal principalAmount, BigDecimal annualInterestRate, Integer termMonths) {
        return strategy.calculateTotalInterest(principalAmount, annualInterestRate, termMonths);
    }

    public String strategyName() {
        return strategy.strategyName();
    }
}
