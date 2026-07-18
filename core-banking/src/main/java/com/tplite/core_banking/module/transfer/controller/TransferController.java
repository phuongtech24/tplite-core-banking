package com.tplite.core_banking.module.transfer.controller;

import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tplite.core_banking.common.response.ApiResponse;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.transfer.dto.TransferDto;
import com.tplite.core_banking.module.transfer.service.TransferService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api")
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfers")
    @PreAuthorize("hasAuthority('TRANSFER_CREATE')")
    public ResponseEntity<ApiResponse<TransferDto>> transferMoney(
            Authentication authentication,
            @Size(max = 100, message = "Idempotency key must not exceed 100 characters") @Pattern(regexp = "^(?!\\s*$).+", message = "Idempotency key must not be blank") @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody TransferDto request
    ) {
        TransferDto response = transferService.transferMoney(authentication.getName(), idempotencyKey, request);
        return ResponseEntity.ok(ApiResponse.success("Transfer success", response));
    }

    @GetMapping("/transactions/my")
    @PreAuthorize("hasAuthority('TRANSACTION_READ_OWN')")
    public ResponseEntity<ApiResponse<PageResponse<TransferDto>>> getMyTransactions(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<TransferDto> response = transferService.getMyTransactions(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Get my transactions success", response));
    }
}
