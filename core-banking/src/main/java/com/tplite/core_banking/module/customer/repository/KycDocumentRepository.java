package com.tplite.core_banking.module.customer.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tplite.core_banking.module.customer.entity.Customer;
import com.tplite.core_banking.module.customer.entity.KycDocument;
import com.tplite.core_banking.module.customer.entity.KycDocumentStatus;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument, UUID> {
    Page<KycDocument> findByCustomer(Customer customer, Pageable pageable);

    @Query("""
            SELECT k
            FROM KycDocument k
            WHERE (:status IS NULL OR k.status = :status)
              AND (
                    :keyword IS NULL
                    OR LOWER(k.documentNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(k.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(k.customer.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<KycDocument> searchKycDocuments(
            @Param("keyword") String keyword,
            @Param("status") KycDocumentStatus status,
            Pageable pageable
    );
}
