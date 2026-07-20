package com.tplite.core_banking.module.customer.controller;

import jakarta.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tplite.core_banking.common.response.ApiResponse;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.common.validation.EnumParser;
import com.tplite.core_banking.common.validation.ValueOfEnum;
import com.tplite.core_banking.module.customer.dto.CreateKycDocumentRequest;
import com.tplite.core_banking.module.customer.dto.CustomerResponse;
import com.tplite.core_banking.module.customer.dto.KycDocumentResponse;
import com.tplite.core_banking.module.customer.dto.ReviewKycDocumentRequest;
import com.tplite.core_banking.module.customer.dto.UpsertCustomerRequest;
import com.tplite.core_banking.module.customer.entity.CustomerStatus;
import com.tplite.core_banking.module.customer.entity.KycDocumentStatus;
import com.tplite.core_banking.module.customer.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customers/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> createMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpsertCustomerRequest request
    ) {
        CustomerResponse response = customerService.createMyCustomerProfile(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Create customer profile success", response));
    }

    @GetMapping("/customers/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getMyProfile(Authentication authentication) {
        CustomerResponse response = customerService.getMyCustomerProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Get customer profile success", response));
    }

    @PutMapping("/customers/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpsertCustomerRequest request
    ) {
        CustomerResponse response = customerService.updateMyCustomerProfile(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Update customer profile success", response));
    }

    @PostMapping("/customers/me/kyc-documents")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<KycDocumentResponse>> createMyKycDocument(
            Authentication authentication,
            @Valid @RequestBody CreateKycDocumentRequest request
    ) {
        KycDocumentResponse response = customerService.createMyKycDocument(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Submit KYC document success", response));
    }

    @GetMapping("/customers/me/kyc-documents")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PageResponse<KycDocumentResponse>>> getMyKycDocuments(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<KycDocumentResponse> response = customerService.getMyKycDocuments(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Get my KYC documents success", response));
    }

    @GetMapping("/staff/customers")
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    public ResponseEntity<ApiResponse<PageResponse<CustomerResponse>>> searchCustomers(
            @Size(max = 100, message = "Keyword must not exceed 100 characters") @RequestParam(required = false) String keyword,
            @ValueOfEnum(enumClass = CustomerStatus.class, message = "Customer status is invalid") @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<CustomerResponse> response = customerService.searchCustomers(keyword, EnumParser.parse(CustomerStatus.class, status), pageable);
        return ResponseEntity.ok(ApiResponse.success("Search customers success", response));
    }

    @GetMapping("/staff/kyc-documents")
    @PreAuthorize("hasAuthority('KYC_REVIEW')")
    public ResponseEntity<ApiResponse<PageResponse<KycDocumentResponse>>> searchKycDocuments(
            @Size(max = 100, message = "Keyword must not exceed 100 characters") @RequestParam(required = false) String keyword,
            @ValueOfEnum(enumClass = KycDocumentStatus.class, message = "KYC document status is invalid") @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<KycDocumentResponse> response = customerService.searchKycDocuments(keyword, EnumParser.parse(KycDocumentStatus.class, status), pageable);
        return ResponseEntity.ok(ApiResponse.success("Search KYC documents success", response));
    }

    @PatchMapping("/staff/kyc-documents/{id}/review")
    @PreAuthorize("hasAuthority('KYC_REVIEW')")
    public ResponseEntity<ApiResponse<KycDocumentResponse>> reviewKycDocument(
            @PathVariable("id") UUID documentId,
            @Valid @RequestBody ReviewKycDocumentRequest request
    ) {
        KycDocumentResponse response = customerService.reviewKycDocument(documentId, request);
        return ResponseEntity.ok(ApiResponse.success("Review KYC document success", response));
    }
}
