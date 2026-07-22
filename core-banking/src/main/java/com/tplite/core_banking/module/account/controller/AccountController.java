package com.tplite.core_banking.module.account.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tplite.core_banking.common.response.ApiResponse;
import com.tplite.core_banking.common.validation.EnumParser;
import com.tplite.core_banking.common.validation.ValueOfEnum;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.account.dto.AccountResponse;
import com.tplite.core_banking.module.account.dto.CreateAccountRequest;
import com.tplite.core_banking.module.account.dto.UpdateAccountStatusRequest;
import com.tplite.core_banking.module.account.entity.AccountStatus;
import com.tplite.core_banking.module.account.service.AccountService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            Authentication authentication,
            @Valid @RequestBody CreateAccountRequest request
    ) {
        AccountResponse response = accountService.createAccount(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Create account success", response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PageResponse<AccountResponse>>> getMyAccounts(
            Authentication authentication,
            @ValueOfEnum(enumClass = AccountStatus.class, message = "Status is invalid") @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<AccountResponse> response = accountService.getMyAccounts(authentication.getName(), EnumParser.parse(AccountStatus.class, status), pageable);
        return ResponseEntity.ok(ApiResponse.success("Get my accounts success", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<AccountResponse>> getMyAccountDetail(
            Authentication authentication,
            @PathVariable("id") UUID accountId
    ) {
        AccountResponse response = accountService.getMyAccountDetail(authentication.getName(), accountId);
        return ResponseEntity.ok(ApiResponse.success("Get account detail success", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccountStatus(
            @PathVariable("id") UUID accountId,
            @Valid @RequestBody UpdateAccountStatusRequest request
    ) {
        AccountResponse response = accountService.updateAccountStatus(accountId, request);
        return ResponseEntity.ok(ApiResponse.success("Update account status success", response));
    }
}
