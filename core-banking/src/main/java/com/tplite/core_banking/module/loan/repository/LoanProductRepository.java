package com.tplite.core_banking.module.loan.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tplite.core_banking.module.loan.entity.LoanProduct;
import com.tplite.core_banking.module.loan.entity.LoanProductStatus;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, UUID> {
    boolean existsByCode(String code);

    Optional<LoanProduct> findByCode(String code);

    List<LoanProduct> findByStatus(LoanProductStatus status);

    Page<LoanProduct> findByStatus(LoanProductStatus status, Pageable pageable);

    @Query("""
            SELECT p
            FROM LoanProduct p
            WHERE (:status IS NULL OR p.status = :status)
              AND (
                    :keyword IS NULL
                    OR LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<LoanProduct> searchProducts(
            @Param("keyword") String keyword,
            @Param("status") LoanProductStatus status,
            Pageable pageable
    );
}
