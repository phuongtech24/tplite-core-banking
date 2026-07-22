package com.tplite.core_banking.module.card.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

import com.tplite.core_banking.common.validation.EnumParser;
import com.tplite.core_banking.common.validation.ValueOfEnum;
import com.tplite.core_banking.module.card.entity.CardType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CreateCardRequest {
    @NotNull(message = "Account id is required")
    private UUID accountId;

    @NotBlank(message = "Card type is required")
    @ValueOfEnum(enumClass = CardType.class, message = "Card type is invalid")
    private String cardType;

    @NotNull(message = "Daily limit is required")
    @DecimalMin(value = "0.00", message = "Daily limit must be greater than or equal to 0")
    private BigDecimal dailyLimit = new BigDecimal("5000000.00");

    public CardType getCardType() {
        return EnumParser.parse(CardType.class, cardType);
    }
}
