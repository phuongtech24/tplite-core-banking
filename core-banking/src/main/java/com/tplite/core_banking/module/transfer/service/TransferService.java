package com.tplite.core_banking.module.transfer.service;

import org.springframework.data.domain.Pageable;

import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.transfer.dto.TransferDto;

public interface TransferService {
    TransferDto transferMoney(String email, TransferDto request);

    TransferDto transferMoney(String email, String idempotencyKey, TransferDto request);

    PageResponse<TransferDto> getMyTransactions(String email, Pageable pageable);
}
