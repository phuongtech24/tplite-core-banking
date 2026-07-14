package com.tplite.core_banking.module.loan.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class SimpleInterestStrategyTest {
    private final SimpleInterestStrategy strategy = new SimpleInterestStrategy();

    @Test
    void calculateTotalInterest_shouldReturnSimpleInterestForLoanTerm() {
        BigDecimal principal = new BigDecimal("12000000.00");
        BigDecimal annualRate = new BigDecimal("12.00");
        int termMonths = 12;

        BigDecimal interest = strategy.calculateTotalInterest(principal, annualRate, termMonths);

        assertThat(interest).isEqualByComparingTo("1440000.00");
        assertThat(strategy.strategyName()).isEqualTo("SIMPLE_INTEREST");
    }
}
