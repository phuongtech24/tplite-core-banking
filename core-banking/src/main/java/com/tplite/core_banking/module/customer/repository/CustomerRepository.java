package com.tplite.core_banking.module.customer.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tplite.core_banking.module.customer.entity.Customer;
import com.tplite.core_banking.module.customer.entity.CustomerStatus;
import com.tplite.core_banking.module.user.entity.User;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByUser(User user);

    boolean existsByCustomerCode(String customerCode);

    @Query("""
            SELECT c
            FROM Customer c
            WHERE (:status IS NULL OR c.status = :status)
              AND (
                    :keyword IS NULL
                    OR LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<Customer> searchCustomers(
            @Param("keyword") String keyword,
            @Param("status") CustomerStatus status,
            Pageable pageable
    );
}
