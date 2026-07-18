package com.tplite.core_banking.module.user.controller;

import jakarta.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tplite.core_banking.common.response.ApiResponse;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.user.dto.UpdateMyProfileRequest;
import com.tplite.core_banking.module.user.dto.UpdateUserStatusRequest;
import com.tplite.core_banking.module.user.dto.UserResponse;
import com.tplite.core_banking.module.user.entity.UserStatus;
import com.tplite.core_banking.module.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(Authentication authentication) {
        UserResponse response = userService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Get current user success", response));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(
            Authentication authentication,
            @Valid @RequestBody UpdateMyProfileRequest request
    ) {
        UserResponse response = userService.updateCurrentUser(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Update current user success", response));
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> searchUsers(
            @Size(max = 100, message = "Keyword must not exceed 100 characters") @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<UserResponse> response = userService.searchUsers(keyword, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search users success", response));
    }

    @PatchMapping("/admin/users/{id}/status")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable("id") UUID userId,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        UserResponse response = userService.updateUserStatus(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Update user status success", response));
    }
}
