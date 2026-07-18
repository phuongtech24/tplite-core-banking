package com.tplite.core_banking.module.card.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.tplite.core_banking.common.validation.EnumParser;
import com.tplite.core_banking.common.validation.ValueOfEnum;
import com.tplite.core_banking.module.card.entity.CardStatus;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCardStatusRequest {
    @NotBlank(message = "Status is required")
    @ValueOfEnum(enumClass = CardStatus.class, message = "Status is invalid")
    private String status;

    public CardStatus getStatus() {
        return EnumParser.parse(CardStatus.class, status);
    }
}
