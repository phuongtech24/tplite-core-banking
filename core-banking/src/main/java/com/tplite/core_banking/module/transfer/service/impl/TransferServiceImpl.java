package com.tplite.core_banking.module.transfer.service.impl;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tplite.core_banking.common.exception.BusinessException;
import com.tplite.core_banking.common.exception.ResourceNotFoundException;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.audit.service.AuditLogService;
import com.tplite.core_banking.module.account.entity.Account;
import com.tplite.core_banking.module.account.entity.AccountStatus;
import com.tplite.core_banking.module.account.repository.AccountRepository;
import com.tplite.core_banking.module.notification.entity.NotificationType;
import com.tplite.core_banking.module.notification.event.NotificationEventPublisher;
import com.tplite.core_banking.module.transfer.dto.TransferDto;
import com.tplite.core_banking.module.transfer.entity.Transaction;
import com.tplite.core_banking.module.transfer.entity.TransactionStatus;
import com.tplite.core_banking.module.transfer.entity.TransactionType;
import com.tplite.core_banking.module.transfer.repository.TransactionRepository;
import com.tplite.core_banking.module.transfer.service.TransferService;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@Service
public class TransferServiceImpl implements TransferService {
    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final NotificationEventPublisher notificationEventPublisher;
    private final AuditLogService auditLogService;

    public TransferServiceImpl(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            NotificationEventPublisher notificationEventPublisher,
            AuditLogService auditLogService
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.notificationEventPublisher = notificationEventPublisher;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransferDto transferMoney(String email, TransferDto request) {
        return transferMoney(email, null, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransferDto transferMoney(String email, String idempotencyKey, TransferDto request) {
        User user = findUserByEmail(email);
        String normalizedKey = normalizeIdempotencyKey(idempotencyKey);
        if (normalizedKey != null) {
            Transaction existingTransaction = transactionRepository.findByIdempotencyKey(normalizedKey)
                    .orElse(null);
            if (existingTransaction != null) {
                validateIdempotentReplay(user, existingTransaction, request);
                log.info("Idempotent transfer replay: idempotencyKey={}, transactionId={}",
                        normalizedKey, existingTransaction.getId());
                return TransferDto.fromEntity(existingTransaction);
            }
        }

        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BusinessException("Source account and destination account must be different");
        }

        Account[] lockedAccounts = lockAccountsInOrder(request.getFromAccountId(), request.getToAccountId());
        Account fromAccount = lockedAccounts[0].getId().equals(request.getFromAccountId()) ? lockedAccounts[0] : lockedAccounts[1];
        Account toAccount = lockedAccounts[0].getId().equals(request.getToAccountId()) ? lockedAccounts[0] : lockedAccounts[1];

        validateTransfer(user, fromAccount, toAccount, request);

        Transaction transaction = new Transaction(
                fromAccount,
                toAccount,
                request.getAmount(),
                TransactionType.TRANSFER,
                TransactionStatus.PENDING
        );
        transaction.setTransactionCode(generateUniqueTransactionCode());
        transaction.setIdempotencyKey(normalizedKey);
        transaction.setCurrency(fromAccount.getCurrency());
        transaction.setDescription(request.getDescription());
        transaction.setCreatedBy(user);

        fromAccount.hold(request.getAmount());
        Transaction pendingTransaction = transactionRepository.save(transaction);

        fromAccount.clear(request.getAmount());
        toAccount.credit(request.getAmount());
        pendingTransaction.setStatus(TransactionStatus.SUCCESS);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction savedTransaction = transactionRepository.save(pendingTransaction);
        createTransferNotifications(fromAccount, toAccount, savedTransaction);
        auditLogService.record(user, "TRANSFER_CREATE", "TRANSACTION", savedTransaction.getId(), "Transfer completed");
        log.info("Transfer completed: transactionId={}, fromAccountId={}, toAccountId={}, amount={}",
                savedTransaction.getId(), fromAccount.getId(), toAccount.getId(), savedTransaction.getAmount());
        return TransferDto.fromEntity(savedTransaction);
    }

    @Override
    public PageResponse<TransferDto> getMyTransactions(String email, Pageable pageable) {
        User user = findUserByEmail(email);
        Page<TransferDto> transactions = transactionRepository.findUserTransactions(user, pageable)
                .map(TransferDto::fromEntity);
        return PageResponse.from(transactions);
    }

    private Account[] lockAccountsInOrder(UUID fromId, UUID toId) {
        UUID minId = fromId.compareTo(toId) < 0 ? fromId : toId;
        UUID maxId = fromId.compareTo(toId) > 0 ? fromId : toId;

        Account firstLock = accountRepository.findByIdWithLock(minId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + minId));
        Account secondLock = accountRepository.findByIdWithLock(maxId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + maxId));

        return new Account[] {firstLock, secondLock};
    }

    private void validateTransfer(User user, Account fromAccount, Account toAccount, TransferDto request) {
        if (!fromAccount.getUser().getId().equals(user.getId())) {
            throw new BusinessException("You can only transfer from your own account");
        }
        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Source account is not active");
        }
        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Destination account is not active");
        }
        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            throw new BusinessException("Currency mismatch");
        }
        if (fromAccount.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient balance");
        }
    }

    private String normalizeIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return null;
        }
        String normalizedKey = idempotencyKey.trim();
        try {
            UUID.fromString(normalizedKey);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Idempotency-Key must be a valid UUID");
        }
        return normalizedKey;
    }

    private void validateIdempotentReplay(User user, Transaction transaction, TransferDto request) {
        if (!transaction.getCreatedBy().getId().equals(user.getId())) {
            throw new BusinessException("Idempotency key belongs to another user");
        }
        if (!transaction.getFromAccount().getId().equals(request.getFromAccountId())
                || !transaction.getToAccount().getId().equals(request.getToAccountId())
                || transaction.getAmount().compareTo(request.getAmount()) != 0) {
            throw new BusinessException("Idempotency key was already used for another transfer");
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private String generateUniqueTransactionCode() {
        String transactionCode;
        do {
            transactionCode = "TX" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000, 9999);
        } while (transactionRepository.existsByTransactionCode(transactionCode));
        return transactionCode;
    }

    private void createTransferNotifications(Account fromAccount, Account toAccount, Transaction transaction) {
        notificationEventPublisher.publishAfterCommit(
                fromAccount.getUser(),
                "Transfer completed",
                "You transferred " + transaction.getAmount() + " " + transaction.getCurrency()
                        + " to account " + toAccount.getAccountNumber(),
                NotificationType.TRANSACTION
        );

        notificationEventPublisher.publishAfterCommit(
                toAccount.getUser(),
                "Money received",
                "You received " + transaction.getAmount() + " " + transaction.getCurrency()
                        + " from account " + fromAccount.getAccountNumber(),
                NotificationType.TRANSACTION
        );
    }
}
