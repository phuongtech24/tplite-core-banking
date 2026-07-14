package com.tplite.core_banking.module.transfer.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tplite.core_banking.module.transfer.entity.Transaction;
import com.tplite.core_banking.module.user.entity.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    boolean existsByTransactionCode(String transactionCode);

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    @Query("""
            SELECT t
            FROM Transaction t
            WHERE t.fromAccount.user = :user
               OR t.toAccount.user = :user
            """)
    Page<Transaction> findUserTransactions(@Param("user") User user, Pageable pageable);
}
