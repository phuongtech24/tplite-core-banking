package com.tplite.core_banking.module.customer.dto;

import com.tplite.core_banking.module.customer.entity.KycDocumentStatus;

import jakarta.validation.constraints.NotNull;

public class ReviewKycDocumentRequest {
    @NotNull(message = "Status is required")
    private KycDocumentStatus status;

    public KycDocumentStatus getStatus() { return status; }
    public void setStatus(KycDocumentStatus status) { this.status = status; }
}
