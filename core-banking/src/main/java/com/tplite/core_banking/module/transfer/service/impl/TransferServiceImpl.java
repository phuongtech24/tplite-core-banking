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

    public TransferServiceImpl(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            NotificationEventPublisher notificationEventPublisher
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransferDto transferMoney(String email, TransferDto request) {
        User user = findUserByEmail(email);

        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BusinessException("Source account and destination account must be different");
        }

        Account[] lockedAccounts = lockAccountsInOrder(request.getFromAccountId(), request.getToAccountId());
        Account fromAccount = lockedAccounts[0].getId().equals(request.getFromAccountId()) ? lockedAccounts[0] : lockedAccounts[1];
        Account toAccount = lockedAccounts[0].getId().equals(request.getToAccountId()) ? lockedAccounts[0] : lockedAccounts[1];

        validateTransfer(user, fromAccount, toAccount, request);

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction(
                fromAccount,
                toAccount,
                request.getAmount(),
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS
        );
        transaction.setTransactionCode(generateUniqueTransactionCode());
        transaction.setCurrency(fromAccount.getCurrency());
        transaction.setDescription(request.getDescription());
        transaction.setCreatedBy(user);

        Transaction savedTransaction = transactionRepository.save(transaction);
        createTransferNotifications(fromAccount, toAccount, savedTransaction);
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
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient balance");
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
