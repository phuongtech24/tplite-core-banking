package com.tplite.core_banking.module.loan.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

@Component
public class SimpleInterestStrategy implements LoanInterestStrategy {
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal MONTHS_PER_YEAR = BigDecimal.valueOf(12);

    @Override
    public String strategyName() {
        return "SIMPLE_INTEREST";
    }

    @Override
    public BigDecimal calculateTotalInterest(BigDecimal principalAmount, BigDecimal annualInterestRate, Integer termMonths) {
        BigDecimal monthlyRate = annualInterestRate
                .divide(ONE_HUNDRED, 8, RoundingMode.HALF_UP)
                .divide(MONTHS_PER_YEAR, 8, RoundingMode.HALF_UP);

        return principalAmount
                .multiply(monthlyRate)
                .multiply(BigDecimal.valueOf(termMonths))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
