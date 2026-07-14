package com.tplite.core_banking.module.card.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.card.dto.CardResponse;
import com.tplite.core_banking.module.card.dto.CreateCardRequest;
import com.tplite.core_banking.module.card.dto.UpdateCardStatusRequest;
import com.tplite.core_banking.module.card.entity.CardStatus;

public interface CardService {
    CardResponse createCard(String email, CreateCardRequest request);

    PageResponse<CardResponse> getMyCards(String email, CardStatus status, Pageable pageable);

    CardResponse getMyCardDetail(String email, UUID cardId);

    CardResponse updateCardStatus(UUID cardId, UpdateCardStatusRequest request);
}
