package com.tplite.core_banking.module.card.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tplite.core_banking.common.response.ApiResponse;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.card.dto.CardResponse;
import com.tplite.core_banking.module.card.dto.CreateCardRequest;
import com.tplite.core_banking.module.card.dto.UpdateCardStatusRequest;
import com.tplite.core_banking.module.card.entity.CardStatus;
import com.tplite.core_banking.module.card.service.CardService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CardResponse>> createCard(
            Authentication authentication,
            @Valid @RequestBody CreateCardRequest request
    ) {
        CardResponse response = cardService.createCard(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Create card success", response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PageResponse<CardResponse>>> getMyCards(
            Authentication authentication,
            @RequestParam(required = false) CardStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<CardResponse> response = cardService.getMyCards(authentication.getName(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Get my cards success", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CardResponse>> getMyCardDetail(
            Authentication authentication,
            @PathVariable("id") UUID cardId
    ) {
        CardResponse response = cardService.getMyCardDetail(authentication.getName(), cardId);
        return ResponseEntity.ok(ApiResponse.success("Get card detail success", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<ApiResponse<CardResponse>> updateCardStatus(
            @PathVariable("id") UUID cardId,
            @Valid @RequestBody UpdateCardStatusRequest request
    ) {
        CardResponse response = cardService.updateCardStatus(cardId, request);
        return ResponseEntity.ok(ApiResponse.success("Update card status success", response));
    }
}
