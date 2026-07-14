package com.tplite.core_banking.module.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tplite.core_banking.common.response.ApiResponse;

@RestController
@RequestMapping("/api/security-test")
public class SecurityTestController {
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<String>> me(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success("Current user", authentication.getName()));
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> customerOnly() {
        return ResponseEntity.ok(ApiResponse.success("Customer endpoint", "Only CUSTOMER can access"));
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<String>> staffOnly() {
        return ResponseEntity.ok(ApiResponse.success("Staff endpoint", "Only STAFF can access"));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> adminOnly() {
        return ResponseEntity.ok(ApiResponse.success("Admin endpoint", "Only ADMIN can access"));
    }

    @GetMapping("/transfer-create")
    @PreAuthorize("hasAuthority('TRANSFER_CREATE')")
    public ResponseEntity<ApiResponse<String>> transferCreatePermission() {
        return ResponseEntity.ok(ApiResponse.success("Permission endpoint", "User has TRANSFER_CREATE permission"));
    }
}
