package com.tplite.core_banking.module.loan.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.loan.dto.CreateLoanProductRequest;
import com.tplite.core_banking.module.loan.dto.CreateLoanRequest;
import com.tplite.core_banking.module.loan.dto.LoanProductResponse;
import com.tplite.core_banking.module.loan.dto.LoanResponse;
import com.tplite.core_banking.module.loan.entity.LoanProductStatus;
import com.tplite.core_banking.module.loan.entity.LoanStatus;

public interface LoanService {
    LoanProductResponse createLoanProduct(CreateLoanProductRequest request);

    PageResponse<LoanProductResponse> searchLoanProducts(String keyword, LoanProductStatus status, Pageable pageable);

    LoanResponse createLoan(String email, CreateLoanRequest request);

    PageResponse<LoanResponse> getMyLoans(String email, Pageable pageable);

    PageResponse<LoanResponse> searchLoans(String keyword, LoanStatus status, Pageable pageable);

    LoanResponse approveLoan(String email, UUID loanId);

    LoanResponse rejectLoan(String email, UUID loanId);
}
