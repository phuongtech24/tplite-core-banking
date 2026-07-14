package com.tplite.core_banking.module.customer.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.customer.dto.CreateKycDocumentRequest;
import com.tplite.core_banking.module.customer.dto.CustomerResponse;
import com.tplite.core_banking.module.customer.dto.KycDocumentResponse;
import com.tplite.core_banking.module.customer.dto.ReviewKycDocumentRequest;
import com.tplite.core_banking.module.customer.dto.UpsertCustomerRequest;
import com.tplite.core_banking.module.customer.entity.CustomerStatus;
import com.tplite.core_banking.module.customer.entity.KycDocumentStatus;

public interface CustomerService {
    CustomerResponse createMyCustomerProfile(String email, UpsertCustomerRequest request);

    CustomerResponse getMyCustomerProfile(String email);

    CustomerResponse updateMyCustomerProfile(String email, UpsertCustomerRequest request);

    KycDocumentResponse createMyKycDocument(String email, CreateKycDocumentRequest request);

    PageResponse<KycDocumentResponse> getMyKycDocuments(String email, Pageable pageable);

    PageResponse<CustomerResponse> searchCustomers(String keyword, CustomerStatus status, Pageable pageable);

    PageResponse<KycDocumentResponse> searchKycDocuments(String keyword, KycDocumentStatus status, Pageable pageable);

    KycDocumentResponse reviewKycDocument(UUID documentId, ReviewKycDocumentRequest request);
}
