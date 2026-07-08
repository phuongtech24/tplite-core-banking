package com.tplite.core_banking.module.transfer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tplite.core_banking.module.transfer.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
