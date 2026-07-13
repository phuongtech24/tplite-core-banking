package com.tplite.core_banking.module.user.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.user.dto.UpdateMyProfileRequest;
import com.tplite.core_banking.module.user.dto.UpdateUserStatusRequest;
import com.tplite.core_banking.module.user.dto.UserResponse;
import com.tplite.core_banking.module.user.entity.UserStatus;

public interface UserService {
    UserResponse getCurrentUser(String email);

    UserResponse updateCurrentUser(String email, UpdateMyProfileRequest request);

    PageResponse<UserResponse> searchUsers(String keyword, UserStatus status, Pageable pageable);

    UserResponse updateUserStatus(UUID userId, UpdateUserStatusRequest request);
}
