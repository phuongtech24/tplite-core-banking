package com.tplite.core_banking.module.customer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.tplite.core_banking.module.customer.entity.KycDocument;
import com.tplite.core_banking.module.customer.entity.KycDocumentStatus;
import com.tplite.core_banking.module.customer.entity.KycDocumentType;

@Getter
@Setter
@NoArgsConstructor
public class KycDocumentResponse {
    private UUID id;
    private UUID customerId;
    private String customerCode;
    private String customerName;
    private KycDocumentType documentType;
    private String documentNumber;
    private LocalDate issuedDate;
    private LocalDate expiredDate;
    private String issuedBy;
    private KycDocumentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static KycDocumentResponse from(KycDocument document) {
        KycDocumentResponse response = new KycDocumentResponse();
        response.setId(document.getId());
        response.setCustomerId(document.getCustomer().getId());
        response.setCustomerCode(document.getCustomer().getCustomerCode());
        response.setCustomerName(document.getCustomer().getFullName());
        response.setDocumentType(document.getDocumentType());
        response.setDocumentNumber(document.getDocumentNumber());
        response.setIssuedDate(document.getIssuedDate());
        response.setExpiredDate(document.getExpiredDate());
        response.setIssuedBy(document.getIssuedBy());
        response.setStatus(document.getStatus());
        response.setCreatedAt(document.getCreatedAt());
        response.setUpdatedAt(document.getUpdatedAt());
        return response;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public KycDocumentType getDocumentType() { return documentType; }
    public void setDocumentType(KycDocumentType documentType) { this.documentType = documentType; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }
    public LocalDate getExpiredDate() { return expiredDate; }
    public void setExpiredDate(LocalDate expiredDate) { this.expiredDate = expiredDate; }
    public String getIssuedBy() { return issuedBy; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }
    public KycDocumentStatus getStatus() { return status; }
    public void setStatus(KycDocumentStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
