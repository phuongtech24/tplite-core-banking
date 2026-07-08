package com.tplite.core_banking.module.transfer.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tplite.core_banking.module.account.entity.Account;
import com.tplite.core_banking.module.account.repository.AccountRepository;
import com.tplite.core_banking.module.transfer.dto.TransferDto;
import com.tplite.core_banking.module.transfer.entity.Transaction;
import com.tplite.core_banking.module.transfer.entity.TransactionStatus;
import com.tplite.core_banking.module.transfer.entity.TransactionType;
import com.tplite.core_banking.module.transfer.repository.TransactionRepository;
import com.tplite.core_banking.module.transfer.service.TransferService;

@Service
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransferServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransferDto transferMoney(TransferDto request) {
        // 1. Áp dụng Lock Order để chống Deadlock
        Long minId = Math.min(request.getFromAccountId(), request.getToAccountId());
        Long maxId = Math.max(request.getFromAccountId(), request.getToAccountId());

        // Lock record có ID nhỏ hơn trước
        Account firstLock = accountRepository.findByIdWithLock(minId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản: " + minId));

        // Lock record có ID lớn hơn sau
        Account secondLock = accountRepository.findByIdWithLock(maxId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản: " + maxId));

        // Xác định ai là người gửi, ai là người nhận từ 2 tài khoản đã lock
        Account fromAccount = (firstLock.getId().equals(request.getFromAccountId())) ? firstLock : secondLock;
        Account toAccount = (firstLock.getId().equals(request.getToAccountId())) ? firstLock : secondLock;

        // 2. Kiểm tra số dư (Business Validation)
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Số dư không đủ để thực hiện giao dịch");
        }

        // 3. Thực hiện thay đổi số dư (Atomicity)
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // 4. Lưu lịch sử giao dịch (Durability)
        Transaction transaction = new Transaction(
                fromAccount, 
                toAccount, 
                request.getAmount(), 
                TransactionType.TRANSFER, 
                TransactionStatus.SUCCESS
        );

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransferDto.fromEntity(savedTransaction);
    }
}
