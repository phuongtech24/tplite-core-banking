package com.tplite.core_banking.module.account.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.tplite.core_banking.module.account.entity.Account;
import com.tplite.core_banking.module.account.entity.AccountStatus;
import com.tplite.core_banking.module.account.entity.AccountType;

@Getter
@Setter
@NoArgsConstructor
public class AccountResponse {
    private UUID id;
    private String accountNumber;
    private AccountType accountType;
    private String currency;
    private BigDecimal balance;
    private BigDecimal frozenAmount;
    private BigDecimal availableBalance;
    private AccountStatus status;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AccountResponse from(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType());
        response.setCurrency(account.getCurrency());
        response.setBalance(account.getBalance());
        response.setFrozenAmount(account.getFrozenAmount());
        response.setAvailableBalance(account.getAvailableBalance());
        response.setStatus(account.getStatus());
        response.setOpenedAt(account.getOpenedAt());
        response.setClosedAt(account.getClosedAt());
        response.setVersion(account.getVersion());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());
        return response;
    }
}
