package com.tplite.core_banking.module.auth.service.impl;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tplite.core_banking.common.exception.BusinessException;
import com.tplite.core_banking.common.exception.DuplicateResourceException;
import com.tplite.core_banking.common.exception.ResourceNotFoundException;
import com.tplite.core_banking.module.auth.dto.AuthResponse;
import com.tplite.core_banking.module.auth.dto.LoginRequest;
import com.tplite.core_banking.module.auth.dto.RefreshTokenRequest;
import com.tplite.core_banking.module.auth.dto.RegisterRequest;
import com.tplite.core_banking.module.auth.entity.RefreshToken;
import com.tplite.core_banking.module.auth.entity.Role;
import com.tplite.core_banking.module.auth.entity.UserRole;
import com.tplite.core_banking.module.auth.repository.RefreshTokenRepository;
import com.tplite.core_banking.module.auth.repository.RoleRepository;
import com.tplite.core_banking.module.auth.repository.UserRoleRepository;
import com.tplite.core_banking.module.auth.security.JwtService;
import com.tplite.core_banking.module.auth.service.AuthService;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final String DEFAULT_ROLE = "CUSTOMER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final int refreshTokenExpirationDays;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService,
            @Value("${app.security.refresh-token.expiration-days}") int refreshTokenExpirationDays
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = createRefreshToken(user);

        log.info("User logged in successfully: {}", user.getEmail());
        return buildAuthResponse(user, getRoleNames(user), accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Refresh token is expired or revoked");
        }

        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateToken(userDetails);

        log.info("Access token refreshed for user: {}", user.getEmail());
        return buildAuthResponse(user, getRoleNames(user), accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
        log.info("Refresh token revoked for user id: {}", refreshToken.getUser().getId());
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName()
        );
        User savedUser = userRepository.save(user);

        Role customerRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new ResourceNotFoundException("Default role CUSTOMER is missing. Check Flyway seed data."));

        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(customerRole);
        userRoleRepository.save(userRole);

        log.info("User registered successfully: {}", savedUser.getEmail());
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
            throw new BusinessException("User has no role");
        }

        return roles;
    }

    private String createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshTokenExpirationDays));
        return refreshTokenRepository.save(refreshToken).getToken();
    }

    private AuthResponse buildAuthResponse(User user, Set<String> roles, String accessToken, String refreshToken) {
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                roles,
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getExpirationSeconds()
        );
    }
}
