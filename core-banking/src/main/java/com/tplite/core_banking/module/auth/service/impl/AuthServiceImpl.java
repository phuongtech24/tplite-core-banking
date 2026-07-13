package com.tplite.core_banking.module.auth.service.impl;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tplite.core_banking.module.auth.dto.AuthResponse;
import com.tplite.core_banking.module.auth.dto.LoginRequest;
import com.tplite.core_banking.module.auth.dto.RegisterRequest;
import com.tplite.core_banking.module.auth.entity.Role;
import com.tplite.core_banking.module.auth.entity.UserRole;
import com.tplite.core_banking.module.auth.repository.RoleRepository;
import com.tplite.core_banking.module.auth.repository.UserRoleRepository;
import com.tplite.core_banking.module.auth.service.AuthService;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {
    private static final String DEFAULT_ROLE = "CUSTOMER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                getRoleNames(user)
        );
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName()
        );
        User savedUser = userRepository.save(user);

        Role customerRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException("Default role CUSTOMER is missing. Check Flyway seed data."));

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(customerRole);
        userRoleRepository.save(userRole);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                Set.of(customerRole.getName())
        );
    }

    private Set<String> getRoleNames(User user) {
        Set<String> roles = userRoleRepository.findByUserId(user.getId())
                .stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (roles.isEmpty()) {
            throw new IllegalStateException("User has no role");
        }

        return roles;
    }
}
