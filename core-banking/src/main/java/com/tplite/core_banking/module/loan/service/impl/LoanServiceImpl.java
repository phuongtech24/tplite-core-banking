package com.tplite.core_banking.module.loan.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import com.tplite.core_banking.module.customer.entity.Customer;
import com.tplite.core_banking.module.customer.entity.CustomerStatus;
import com.tplite.core_banking.module.customer.repository.CustomerRepository;
import com.tplite.core_banking.module.loan.dto.CreateLoanProductRequest;
import com.tplite.core_banking.module.loan.dto.CreateLoanRequest;
import com.tplite.core_banking.module.loan.dto.LoanProductResponse;
import com.tplite.core_banking.module.loan.dto.LoanResponse;
import com.tplite.core_banking.module.loan.entity.Loan;
import com.tplite.core_banking.module.loan.entity.LoanProduct;
import com.tplite.core_banking.module.loan.entity.LoanProductStatus;
import com.tplite.core_banking.module.loan.entity.LoanStatus;
import com.tplite.core_banking.module.loan.repository.LoanProductRepository;
import com.tplite.core_banking.module.loan.repository.LoanRepository;
import com.tplite.core_banking.module.loan.service.LoanService;
import com.tplite.core_banking.module.loan.strategy.LoanInterestCalculator;
import com.tplite.core_banking.module.notification.entity.NotificationType;
import com.tplite.core_banking.module.notification.event.NotificationEventPublisher;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@Service
public class LoanServiceImpl implements LoanService {
    private static final Logger log = LoggerFactory.getLogger(LoanServiceImpl.class);

    private final LoanProductRepository loanProductRepository;
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final LoanInterestCalculator interestCalculator;
    private final NotificationEventPublisher notificationEventPublisher;

    public LoanServiceImpl(
            LoanProductRepository loanProductRepository,
            LoanRepository loanRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            LoanInterestCalculator interestCalculator,
            NotificationEventPublisher notificationEventPublisher
    ) {
        this.loanProductRepository = loanProductRepository;
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.interestCalculator = interestCalculator;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    @Override
    @Transactional
    public LoanProductResponse createLoanProduct(CreateLoanProductRequest request) {
        if (loanProductRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Loan product code already exists");
        }
        validateProductRange(request.getMinAmount(), request.getMaxAmount(), request.getMinTermMonths(), request.getMaxTermMonths());

        LoanProduct product = new LoanProduct();
        product.setCode(request.getCode());
        product.setName(request.getName());
        product.setInterestRate(request.getInterestRate());
        product.setMinAmount(request.getMinAmount());
        product.setMaxAmount(request.getMaxAmount());
        product.setMinTermMonths(request.getMinTermMonths());
        product.setMaxTermMonths(request.getMaxTermMonths());
        product.setStatus(LoanProductStatus.ACTIVE);

        LoanProduct savedProduct = loanProductRepository.save(product);
        log.info("Loan product created: productId={}, code={}", savedProduct.getId(), savedProduct.getCode());
        return LoanProductResponse.from(savedProduct);
    }

    @Override
    public PageResponse<LoanProductResponse> searchLoanProducts(String keyword, LoanProductStatus status, Pageable pageable) {
        Page<LoanProductResponse> products = loanProductRepository.searchProducts(normalizeKeyword(keyword), status, pageable)
                .map(LoanProductResponse::from);
        return PageResponse.from(products);
    }

    @Override
    @Transactional
    public LoanResponse createLoan(String email, CreateLoanRequest request) {
        User user = findUserByEmail(email);
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new BusinessException("Customer must pass KYC before applying for a loan");
        }

        LoanProduct product = loanProductRepository.findById(request.getLoanProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found"));

        if (product.getStatus() != LoanProductStatus.ACTIVE) {
            throw new BusinessException("Loan product is not active");
        }
        validateLoanRequest(product, request);

        Loan loan = new Loan();
        loan.setLoanCode(generateUniqueLoanCode());
        loan.setCustomer(customer);
        loan.setLoanProduct(product);
        loan.setPrincipalAmount(request.getPrincipalAmount());
        loan.setInterestRate(product.getInterestRate());
        loan.setTermMonths(request.getTermMonths());
        loan.setOutstandingBalance(request.getPrincipalAmount());
        loan.setStatus(LoanStatus.PENDING_REVIEW);

        Loan savedLoan = loanRepository.save(loan);
        log.info("Loan application created: loanId={}, customerId={}", savedLoan.getId(), customer.getId());
        return toResponse(savedLoan);
    }

    @Override
    public PageResponse<LoanResponse> getMyLoans(String email, Pageable pageable) {
        User user = findUserByEmail(email);
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        Page<LoanResponse> loans = loanRepository.findByCustomer(customer, pageable)
                .map(this::toResponse);
        return PageResponse.from(loans);
    }

    @Override
    public PageResponse<LoanResponse> searchLoans(String keyword, LoanStatus status, Pageable pageable) {
        Page<LoanResponse> loans = loanRepository.searchLoans(normalizeKeyword(keyword), status, pageable)
                .map(this::toResponse);
        return PageResponse.from(loans);
    }

    @Override
    @Transactional
    public LoanResponse approveLoan(String email, UUID loanId) {
        User approver = findUserByEmail(email);
        Loan loan = findLoan(loanId);
        if (loan.getStatus() != LoanStatus.PENDING_REVIEW) {
            throw new BusinessException("Only PENDING_REVIEW loan can be approved");
        }

        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovedBy(approver);
        loan.setApprovedAt(LocalDateTime.now());
        Loan savedLoan = loanRepository.save(loan);
        notificationEventPublisher.publishAfterCommit(
                savedLoan.getCustomer().getUser(),
                "Loan approved",
                "Your loan application " + savedLoan.getLoanCode() + " has been approved",
                NotificationType.LOAN
        );
        log.info("Loan approved: loanId={}, approvedBy={}", savedLoan.getId(), approver.getId());
        return toResponse(savedLoan);
    }

    @Override
    @Transactional
    public LoanResponse rejectLoan(String email, UUID loanId) {
        User approver = findUserByEmail(email);
        Loan loan = findLoan(loanId);
        if (loan.getStatus() != LoanStatus.PENDING_REVIEW) {
            throw new BusinessException("Only PENDING_REVIEW loan can be rejected");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loan.setApprovedBy(approver);
        loan.setApprovedAt(LocalDateTime.now());
        Loan savedLoan = loanRepository.save(loan);
        notificationEventPublisher.publishAfterCommit(
                savedLoan.getCustomer().getUser(),
                "Loan rejected",
                "Your loan application " + savedLoan.getLoanCode() + " has been rejected",
                NotificationType.LOAN
        );
        log.info("Loan rejected: loanId={}, rejectedBy={}", savedLoan.getId(), approver.getId());
        return toResponse(savedLoan);
    }

    private LoanResponse toResponse(Loan loan) {
        BigDecimal totalInterest = interestCalculator.calculateTotalInterest(
                loan.getPrincipalAmount(),
                loan.getInterestRate(),
                loan.getTermMonths()
        );
        return LoanResponse.from(loan, totalInterest, interestCalculator.strategyName());
    }

    private Loan findLoan(UUID loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateProductRange(BigDecimal minAmount, BigDecimal maxAmount, Integer minTerm, Integer maxTerm) {
        if (minAmount.compareTo(maxAmount) > 0) {
            throw new BusinessException("Min amount must be less than or equal to max amount");
        }
        if (minTerm > maxTerm) {
            throw new BusinessException("Min term must be less than or equal to max term");
        }
    }

    private void validateLoanRequest(LoanProduct product, CreateLoanRequest request) {
        if (request.getPrincipalAmount().compareTo(product.getMinAmount()) < 0
                || request.getPrincipalAmount().compareTo(product.getMaxAmount()) > 0) {
            throw new BusinessException("Principal amount is outside product range");
        }
        if (request.getTermMonths() < product.getMinTermMonths()
                || request.getTermMonths() > product.getMaxTermMonths()) {
            throw new BusinessException("Term months is outside product range");
        }
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }

    private String generateUniqueLoanCode() {
        String loanCode;
        do {
            loanCode = "LN" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000, 9999);
        } while (loanRepository.existsByLoanCode(loanCode));
        return loanCode;
    }
}
