package com.tplite.core_banking.module.customer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

import com.tplite.core_banking.common.validation.EnumParser;
import com.tplite.core_banking.common.validation.ValueOfEnum;
import com.tplite.core_banking.module.customer.entity.KycDocumentType;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class CreateKycDocumentRequest {
    @NotBlank(message = "Document type is required")
    @ValueOfEnum(enumClass = KycDocumentType.class, message = "Document type is invalid")
    private String documentType;

    @NotBlank(message = "Document number is required")
    @Size(max = 50, message = "Document number must not exceed 50 characters")
    private String documentNumber;

    @PastOrPresent(message = "Issued date must not be in the future")
    private LocalDate issuedDate;

    @FutureOrPresent(message = "Expired date must not be in the past")
    private LocalDate expiredDate;

    @Size(max = 255, message = "Issued by must not exceed 255 characters")
    private String issuedBy;

    public KycDocumentType getDocumentType() { return EnumParser.parse(KycDocumentType.class, documentType); }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }
    public LocalDate getExpiredDate() { return expiredDate; }
    public void setExpiredDate(LocalDate expiredDate) { this.expiredDate = expiredDate; }
    public String getIssuedBy() { return issuedBy; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }
}
