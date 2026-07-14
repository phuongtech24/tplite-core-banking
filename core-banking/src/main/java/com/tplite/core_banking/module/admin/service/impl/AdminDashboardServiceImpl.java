package com.tplite.core_banking.module.admin.service.impl;

import org.springframework.stereotype.Service;

import com.tplite.core_banking.module.account.repository.AccountRepository;
import com.tplite.core_banking.module.admin.dto.AdminDashboardResponse;
import com.tplite.core_banking.module.admin.service.AdminDashboardService;
import com.tplite.core_banking.module.audit.repository.AuditLogRepository;
import com.tplite.core_banking.module.card.repository.CardRepository;
import com.tplite.core_banking.module.customer.repository.CustomerRepository;
import com.tplite.core_banking.module.loan.repository.LoanRepository;
import com.tplite.core_banking.module.notification.repository.NotificationRepository;
import com.tplite.core_banking.module.transfer.repository.TransactionRepository;
import com.tplite.core_banking.module.user.repository.UserRepository;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminDashboardServiceImpl(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            AccountRepository accountRepository,
            CardRepository cardRepository,
            TransactionRepository transactionRepository,
            LoanRepository loanRepository,
            NotificationRepository notificationRepository,
            AuditLogRepository auditLogRepository
    ) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.loanRepository = loanRepository;
        this.notificationRepository = notificationRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AdminDashboardResponse getDashboard() {
        return new AdminDashboardResponse(
                userRepository.count(),
                customerRepository.count(),
                accountRepository.count(),
                cardRepository.count(),
                transactionRepository.count(),
                loanRepository.count(),
                notificationRepository.count(),
                auditLogRepository.count()
        );
    }
}
