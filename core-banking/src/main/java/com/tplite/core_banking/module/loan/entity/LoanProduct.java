package com.tplite.core_banking.module.loan.entity;

import com.tplite.core_banking.common.entity.BaseEntity;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "loan_products")
public class LoanProduct extends BaseEntity {
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "min_amount", precision = 19, scale = 2)
    private BigDecimal minAmount;

    @Column(name = "max_amount", precision = 19, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "min_term_months")
    private Integer minTermMonths;

    @Column(name = "max_term_months")
    private Integer maxTermMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoanProductStatus status = LoanProductStatus.ACTIVE;
}
