package com.tplite.core_banking.module.loan.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tplite.core_banking.module.customer.entity.Customer;
import com.tplite.core_banking.module.loan.entity.Loan;
import com.tplite.core_banking.module.loan.entity.LoanStatus;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    boolean existsByLoanCode(String loanCode);

    Page<Loan> findByCustomer(Customer customer, Pageable pageable);

    @Query("""
            SELECT l
            FROM Loan l
            WHERE (:status IS NULL OR l.status = :status)
              AND (
                    :keyword IS NULL
                    OR LOWER(l.loanCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(l.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(l.customer.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<Loan> searchLoans(
            @Param("keyword") String keyword,
            @Param("status") LoanStatus status,
            Pageable pageable
    );
}
