package com.tplite.core_banking.module.user.service.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tplite.core_banking.common.exception.ResourceNotFoundException;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.user.dto.UpdateMyProfileRequest;
import com.tplite.core_banking.module.user.dto.UpdateUserStatusRequest;
import com.tplite.core_banking.module.user.dto.UserResponse;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.entity.UserStatus;
import com.tplite.core_banking.module.user.repository.UserRepository;
import com.tplite.core_banking.module.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse getCurrentUser(String email) {
        User user = findByEmail(email);
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUser(String email, UpdateMyProfileRequest request) {
        User user = findByEmail(email);
        user.setFullName(request.getFullName());
        User savedUser = userRepository.save(user);
        log.info("User profile updated: {}", savedUser.getEmail());
        return UserResponse.from(savedUser);
    }

    @Override
    public PageResponse<UserResponse> searchUsers(String keyword, UserStatus status, Pageable pageable) {
        String normalizedKeyword = normalizeKeyword(keyword);
        Page<UserResponse> users = userRepository.searchUsers(normalizedKeyword, status, pageable)
                .map(UserResponse::from);
        return PageResponse.from(users);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(UUID userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setStatus(request.getStatus());
        User savedUser = userRepository.save(user);
        log.info("User status updated: userId={}, status={}", savedUser.getId(), savedUser.getStatus());
        return UserResponse.from(savedUser);
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }
}
