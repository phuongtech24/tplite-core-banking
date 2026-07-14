package com.tplite.core_banking.module.card.service.impl;

import java.time.LocalDate;
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
import com.tplite.core_banking.common.exception.ResourceNotFoundException;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.account.entity.Account;
import com.tplite.core_banking.module.account.entity.AccountStatus;
import com.tplite.core_banking.module.account.repository.AccountRepository;
import com.tplite.core_banking.module.card.dto.CardResponse;
import com.tplite.core_banking.module.card.dto.CreateCardRequest;
import com.tplite.core_banking.module.card.dto.UpdateCardStatusRequest;
import com.tplite.core_banking.module.card.entity.Card;
import com.tplite.core_banking.module.card.entity.CardStatus;
import com.tplite.core_banking.module.card.repository.CardRepository;
import com.tplite.core_banking.module.card.service.CardService;
import com.tplite.core_banking.module.customer.entity.Customer;
import com.tplite.core_banking.module.customer.entity.CustomerStatus;
import com.tplite.core_banking.module.customer.repository.CustomerRepository;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@Service
public class CardServiceImpl implements CardService {
    private static final Logger log = LoggerFactory.getLogger(CardServiceImpl.class);

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CardServiceImpl(
            CardRepository cardRepository,
            AccountRepository accountRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository
    ) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CardResponse createCard(String email, CreateCardRequest request) {
        User user = findUserByEmail(email);
        Customer customer = findCustomerByUser(user);
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new BusinessException("Customer must pass KYC before issuing card");
        }

        Account account = accountRepository.findByIdAndUser(request.getAccountId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Account must be active before issuing card");
        }

        Card card = new Card();
        card.setCustomer(customer);
        card.setAccount(account);
        card.setCardNumberMasked(generateMaskedCardNumber());
        card.setCardType(request.getCardType());
        card.setStatus(CardStatus.ACTIVE);
        card.setDailyLimit(request.getDailyLimit());
        card.setIssuedAt(LocalDateTime.now());
        card.setExpiredAt(LocalDate.now().plusYears(5));

        Card savedCard = cardRepository.save(card);
        log.info("Card issued: cardId={}, customerId={}, accountId={}", savedCard.getId(), customer.getId(), account.getId());
        return CardResponse.from(savedCard);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CardResponse> getMyCards(String email, CardStatus status, Pageable pageable) {
        Customer customer = findCustomerByUser(findUserByEmail(email));
        Page<CardResponse> cards = status == null
                ? cardRepository.findByCustomer(customer, pageable).map(CardResponse::from)
                : cardRepository.findByCustomerAndStatus(customer, status, pageable).map(CardResponse::from);
        return PageResponse.from(cards);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponse getMyCardDetail(String email, UUID cardId) {
        Customer customer = findCustomerByUser(findUserByEmail(email));
        Card card = cardRepository.findByIdAndCustomer(cardId, customer)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        return CardResponse.from(card);
    }

    @Override
    @Transactional
    public CardResponse updateCardStatus(UUID cardId, UpdateCardStatusRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setStatus(request.getStatus());
        Card savedCard = cardRepository.save(card);
        log.info("Card status updated: cardId={}, status={}", savedCard.getId(), savedCard.getStatus());
        return CardResponse.from(savedCard);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Customer findCustomerByUser(User user) {
        return customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
    }

    private String generateMaskedCardNumber() {
        long lastFour = ThreadLocalRandom.current().nextLong(1000, 9999);
        return "9704********" + lastFour;
    }
}
