package com.tplite.core_banking.module.loan.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tplite.core_banking.common.response.ApiResponse;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.loan.dto.CreateLoanProductRequest;
import com.tplite.core_banking.module.loan.dto.CreateLoanRequest;
import com.tplite.core_banking.module.loan.dto.LoanProductResponse;
import com.tplite.core_banking.module.loan.dto.LoanResponse;
import com.tplite.core_banking.module.loan.entity.LoanProductStatus;
import com.tplite.core_banking.module.loan.entity.LoanStatus;
import com.tplite.core_banking.module.loan.service.LoanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/admin/loan-products")
    @PreAuthorize("hasAuthority('LOAN_APPROVE')")
    public ResponseEntity<ApiResponse<LoanProductResponse>> createLoanProduct(
            @Valid @RequestBody CreateLoanProductRequest request
    ) {
        LoanProductResponse response = loanService.createLoanProduct(request);
        return ResponseEntity.ok(ApiResponse.success("Create loan product success", response));
    }

    @GetMapping("/loan-products")
    public ResponseEntity<ApiResponse<PageResponse<LoanProductResponse>>> searchActiveLoanProducts(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<LoanProductResponse> response = loanService.searchLoanProducts(keyword, LoanProductStatus.ACTIVE, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search active loan products success", response));
    }

    @GetMapping("/admin/loan-products")
    @PreAuthorize("hasAuthority('LOAN_APPROVE')")
    public ResponseEntity<ApiResponse<PageResponse<LoanProductResponse>>> searchLoanProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LoanProductStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<LoanProductResponse> response = loanService.searchLoanProducts(keyword, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search loan products success", response));
    }

    @PostMapping("/loans")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<LoanResponse>> createLoan(
            Authentication authentication,
            @Valid @RequestBody CreateLoanRequest request
    ) {
        LoanResponse response = loanService.createLoan(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Create loan application success", response));
    }

    @GetMapping("/loans/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PageResponse<LoanResponse>>> getMyLoans(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<LoanResponse> response = loanService.getMyLoans(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Get my loans success", response));
    }

    @GetMapping("/staff/loans")
    @PreAuthorize("hasAuthority('LOAN_REVIEW') or hasAuthority('LOAN_APPROVE')")
    public ResponseEntity<ApiResponse<PageResponse<LoanResponse>>> searchLoans(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LoanStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<LoanResponse> response = loanService.searchLoans(keyword, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search loans success", response));
    }

    @PatchMapping("/admin/loans/{id}/approve")
    @PreAuthorize("hasAuthority('LOAN_APPROVE')")
    public ResponseEntity<ApiResponse<LoanResponse>> approveLoan(
            Authentication authentication,
            @PathVariable("id") UUID loanId
    ) {
        LoanResponse response = loanService.approveLoan(authentication.getName(), loanId);
        return ResponseEntity.ok(ApiResponse.success("Approve loan success", response));
    }

    @PatchMapping("/admin/loans/{id}/reject")
    @PreAuthorize("hasAuthority('LOAN_APPROVE')")
    public ResponseEntity<ApiResponse<LoanResponse>> rejectLoan(
            Authentication authentication,
            @PathVariable("id") UUID loanId
    ) {
        LoanResponse response = loanService.rejectLoan(authentication.getName(), loanId);
        return ResponseEntity.ok(ApiResponse.success("Reject loan success", response));
    }
}
