package com.tplite.core_banking.module.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tplite.core_banking.module.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, java.util.UUID> {
    Optional<RefreshToken> findByToken(String token);
}
