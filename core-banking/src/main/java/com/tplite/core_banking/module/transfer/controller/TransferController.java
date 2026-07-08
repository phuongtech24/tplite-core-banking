package com.tplite.core_banking.module.transfer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tplite.core_banking.common.response.ApiResponse;
import com.tplite.core_banking.module.transfer.dto.TransferDto;
import com.tplite.core_banking.module.transfer.service.TransferService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransferDto>> transferMoney(@Valid @RequestBody TransferDto request) {
        try {
            TransferDto response = transferService.transferMoney(request);
            return ResponseEntity.ok(ApiResponse.success("Chuyển tiền thành công", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, "Lỗi hệ thống"));
        }
    }
}
