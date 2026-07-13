package com.tplite.core_banking.module.account.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tplite.core_banking.common.exception.ResourceNotFoundException;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.account.dto.AccountResponse;
import com.tplite.core_banking.module.account.dto.CreateAccountRequest;
import com.tplite.core_banking.module.account.dto.UpdateAccountStatusRequest;
import com.tplite.core_banking.module.account.entity.Account;
import com.tplite.core_banking.module.account.entity.AccountStatus;
import com.tplite.core_banking.module.account.repository.AccountRepository;
import com.tplite.core_banking.module.account.service.AccountService;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@Service
public class AccountServiceImpl implements AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AccountResponse createAccount(String email, CreateAccountRequest request) {
        User user = findUserByEmail(email);

        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency());
        account.setBalance(request.getInitialBalance());
        account.setStatus(AccountStatus.ACTIVE);
        account.setOpenedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);
        log.info("Account created: userId={}, accountId={}, accountNumber={}", user.getId(), savedAccount.getId(), savedAccount.getAccountNumber());
        return AccountResponse.from(savedAccount);
    }

    @Override
    public PageResponse<AccountResponse> getMyAccounts(String email, AccountStatus status, Pageable pageable) {
        User user = findUserByEmail(email);
        Page<AccountResponse> accounts = status == null
                ? accountRepository.findByUser(user, pageable).map(AccountResponse::from)
                : accountRepository.findByUserAndStatus(user, status, pageable).map(AccountResponse::from);
        return PageResponse.from(accounts);
    }

    @Override
    public AccountResponse getMyAccountDetail(String email, UUID accountId) {
        User user = findUserByEmail(email);
        Account account = accountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return AccountResponse.from(account);
    }

    @Override
    @Transactional
    public AccountResponse updateAccountStatus(UUID accountId, UpdateAccountStatusRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        account.setStatus(request.getStatus());
        if (request.getStatus() == AccountStatus.CLOSED && account.getClosedAt() == null) {
            account.setClosedAt(LocalDateTime.now());
        }
        if (request.getStatus() != AccountStatus.CLOSED) {
            account.setClosedAt(null);
        }

        Account savedAccount = accountRepository.save(account);
        log.info("Account status updated: accountId={}, status={}", savedAccount.getId(), savedAccount.getStatus());
        return AccountResponse.from(savedAccount);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "10" + ThreadLocalRandom.current().nextLong(1_000_000_000L, 9_999_999_999L);
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
