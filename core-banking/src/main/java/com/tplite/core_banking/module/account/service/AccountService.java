package com.tplite.core_banking.module.account.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.account.dto.AccountResponse;
import com.tplite.core_banking.module.account.dto.CreateAccountRequest;
import com.tplite.core_banking.module.account.dto.UpdateAccountStatusRequest;
import com.tplite.core_banking.module.account.entity.AccountStatus;

public interface AccountService {
    AccountResponse createAccount(String email, CreateAccountRequest request);

    PageResponse<AccountResponse> getMyAccounts(String email, AccountStatus status, Pageable pageable);

    AccountResponse getMyAccountDetail(String email, UUID accountId);

    AccountResponse updateAccountStatus(UUID accountId, UpdateAccountStatusRequest request);
}
