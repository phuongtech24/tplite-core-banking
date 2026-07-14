package com.tplite.core_banking.module.card.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tplite.core_banking.module.card.entity.Card;
import com.tplite.core_banking.module.card.entity.CardStatus;
import com.tplite.core_banking.module.customer.entity.Customer;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    Page<Card> findByCustomer(Customer customer, Pageable pageable);

    Page<Card> findByCustomerAndStatus(Customer customer, CardStatus status, Pageable pageable);

    Optional<Card> findByIdAndCustomer(UUID id, Customer customer);
}
