package com.tplite.core_banking.module.customer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.tplite.core_banking.common.validation.EnumParser;
import com.tplite.core_banking.common.validation.ValueOfEnum;
import com.tplite.core_banking.module.customer.entity.KycDocumentStatus;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class ReviewKycDocumentRequest {
    @NotBlank(message = "Status is required")
    @ValueOfEnum(enumClass = KycDocumentStatus.class, message = "Status is invalid")
    private String status;

    public KycDocumentStatus getStatus() { return EnumParser.parse(KycDocumentStatus.class, status); }
    public void setStatus(String status) { this.status = status; }
}
