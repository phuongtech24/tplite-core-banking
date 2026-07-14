package com.tplite.core_banking.module.customer.service.impl;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tplite.core_banking.common.exception.BusinessException;
import com.tplite.core_banking.common.exception.DuplicateResourceException;
import com.tplite.core_banking.common.exception.ResourceNotFoundException;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.customer.dto.CreateKycDocumentRequest;
import com.tplite.core_banking.module.customer.dto.CustomerResponse;
import com.tplite.core_banking.module.customer.dto.KycDocumentResponse;
import com.tplite.core_banking.module.customer.dto.ReviewKycDocumentRequest;
import com.tplite.core_banking.module.customer.dto.UpsertCustomerRequest;
import com.tplite.core_banking.module.customer.entity.Customer;
import com.tplite.core_banking.module.customer.entity.CustomerStatus;
import com.tplite.core_banking.module.customer.entity.KycDocument;
import com.tplite.core_banking.module.customer.entity.KycDocumentStatus;
import com.tplite.core_banking.module.customer.repository.CustomerRepository;
import com.tplite.core_banking.module.customer.repository.KycDocumentRepository;
import com.tplite.core_banking.module.customer.service.CustomerService;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private final UserRepository userRepository;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            KycDocumentRepository kycDocumentRepository,
            UserRepository userRepository
    ) {
        this.customerRepository = customerRepository;
        this.kycDocumentRepository = kycDocumentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CustomerResponse createMyCustomerProfile(String email, UpsertCustomerRequest request) {
        User user = findUserByEmail(email);
        if (customerRepository.findByUser(user).isPresent()) {
            throw new DuplicateResourceException("Customer profile already exists");
        }

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setCustomerCode(generateUniqueCustomerCode());
        applyCustomerFields(customer, request);
        customer.setStatus(CustomerStatus.PENDING_KYC);

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer profile created: customerId={}, userId={}", savedCustomer.getId(), user.getId());
        return CustomerResponse.from(savedCustomer);
    }

    @Override
    public CustomerResponse getMyCustomerProfile(String email) {
        return CustomerResponse.from(findCustomerByUserEmail(email));
    }

    @Override
    @Transactional
    public CustomerResponse updateMyCustomerProfile(String email, UpsertCustomerRequest request) {
        Customer customer = findCustomerByUserEmail(email);
        applyCustomerFields(customer, request);
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer profile updated: customerId={}", savedCustomer.getId());
        return CustomerResponse.from(savedCustomer);
    }

    @Override
    @Transactional
    public KycDocumentResponse createMyKycDocument(String email, CreateKycDocumentRequest request) {
        Customer customer = findCustomerByUserEmail(email);

        KycDocument document = new KycDocument();
        document.setCustomer(customer);
        document.setDocumentType(request.getDocumentType());
        document.setDocumentNumber(request.getDocumentNumber());
        document.setIssuedDate(request.getIssuedDate());
        document.setExpiredDate(request.getExpiredDate());
        document.setIssuedBy(request.getIssuedBy());
        document.setStatus(KycDocumentStatus.PENDING);

        KycDocument savedDocument = kycDocumentRepository.save(document);
        log.info("KYC document submitted: documentId={}, customerId={}", savedDocument.getId(), customer.getId());
        return KycDocumentResponse.from(savedDocument);
    }

    @Override
    public PageResponse<KycDocumentResponse> getMyKycDocuments(String email, Pageable pageable) {
        Customer customer = findCustomerByUserEmail(email);
        Page<KycDocumentResponse> documents = kycDocumentRepository.findByCustomer(customer, pageable)
                .map(KycDocumentResponse::from);
        return PageResponse.from(documents);
    }

    @Override
    public PageResponse<CustomerResponse> searchCustomers(String keyword, CustomerStatus status, Pageable pageable) {
        Page<CustomerResponse> customers = customerRepository.searchCustomers(normalizeKeyword(keyword), status, pageable)
                .map(CustomerResponse::from);
        return PageResponse.from(customers);
    }

    @Override
    public PageResponse<KycDocumentResponse> searchKycDocuments(String keyword, KycDocumentStatus status, Pageable pageable) {
        Page<KycDocumentResponse> documents = kycDocumentRepository.searchKycDocuments(normalizeKeyword(keyword), status, pageable)
                .map(KycDocumentResponse::from);
        return PageResponse.from(documents);
    }

    @Override
    @Transactional
    public KycDocumentResponse reviewKycDocument(UUID documentId, ReviewKycDocumentRequest request) {
        if (request.getStatus() != KycDocumentStatus.VERIFIED && request.getStatus() != KycDocumentStatus.REJECTED) {
            throw new BusinessException("KYC review status must be VERIFIED or REJECTED");
        }

        KycDocument document = kycDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC document not found"));

        document.setStatus(request.getStatus());
        if (request.getStatus() == KycDocumentStatus.VERIFIED) {
            document.getCustomer().setStatus(CustomerStatus.ACTIVE);
        }
        KycDocument savedDocument = kycDocumentRepository.save(document);
        log.info("KYC document reviewed: documentId={}, status={}", savedDocument.getId(), savedDocument.getStatus());
        return KycDocumentResponse.from(savedDocument);
    }

    private void applyCustomerFields(Customer customer, UpsertCustomerRequest request) {
        customer.setFullName(request.getFullName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
    }

    private Customer findCustomerByUserEmail(String email) {
        User user = findUserByEmail(email);
        return customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }

    private String generateUniqueCustomerCode() {
        String customerCode;
        do {
            customerCode = "CUS" + ThreadLocalRandom.current().nextLong(1_000_000_000L, 9_999_999_999L);
        } while (customerRepository.existsByCustomerCode(customerCode));
        return customerCode;
    }
}
