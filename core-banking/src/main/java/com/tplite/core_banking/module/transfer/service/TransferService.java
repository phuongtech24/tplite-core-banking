package com.tplite.core_banking.module.transfer.service;

import com.tplite.core_banking.module.transfer.dto.TransferDto;

public interface TransferService {
    TransferDto transferMoney(TransferDto request);
}
