package com.tplite.core_banking.module.transfer.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tplite.core_banking.common.exception.BusinessException;
import com.tplite.core_banking.module.account.entity.Account;
import com.tplite.core_banking.module.account.entity.AccountStatus;
import com.tplite.core_banking.module.account.repository.AccountRepository;
import com.tplite.core_banking.module.audit.service.AuditLogService;
import com.tplite.core_banking.module.notification.event.NotificationEventPublisher;
import com.tplite.core_banking.module.transfer.dto.TransferDto;
import com.tplite.core_banking.module.transfer.entity.Transaction;
import com.tplite.core_banking.module.transfer.repository.TransactionRepository;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationEventPublisher notificationEventPublisher;

    @Mock
    private AuditLogService auditLogService;

    @Test
    void transferMoney_shouldRejectWhenBalanceIsInsufficient() {
        TransferServiceImpl service = new TransferServiceImpl(
                accountRepository,
                transactionRepository,
                userRepository,
                notificationEventPublisher,
                auditLogService
        );

        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID fromAccountId = UUID.fromString("00000000-0000-0000-0000-000000000101");
        UUID toAccountId = UUID.fromString("00000000-0000-0000-0000-000000000102");

        User user = new User("customer@tplite.vn", "hashed", "Demo Customer");
        user.setId(userId);

        Account fromAccount = new Account();
        fromAccount.setId(fromAccountId);
        fromAccount.setUser(user);
        fromAccount.setStatus(AccountStatus.ACTIVE);
        fromAccount.setCurrency("VND");
        fromAccount.setBalance(new BigDecimal("100000.00"));

        Account toAccount = new Account();
        toAccount.setId(toAccountId);
        toAccount.setUser(user);
        toAccount.setStatus(AccountStatus.ACTIVE);
        toAccount.setCurrency("VND");
        toAccount.setBalance(new BigDecimal("50000.00"));

        TransferDto request = new TransferDto();
        request.setFromAccountId(fromAccountId);
        request.setToAccountId(toAccountId);
        request.setAmount(new BigDecimal("200000.00"));

        when(userRepository.findByEmail("customer@tplite.vn")).thenReturn(Optional.of(user));
        when(accountRepository.findByIdWithLock(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdWithLock(toAccountId)).thenReturn(Optional.of(toAccount));

        assertThatThrownBy(() -> service.transferMoney("customer@tplite.vn", request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Insufficient balance");

        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(notificationEventPublisher, never()).publishAfterCommit(any(), any(), any(), any());
        verify(auditLogService, never()).record(any(), any(), any(), any(), any());
    }
}
