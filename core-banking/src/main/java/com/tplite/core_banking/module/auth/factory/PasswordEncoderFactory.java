package com.tplite.core_banking.module.auth.factory;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class PasswordEncoderFactory {
    private PasswordEncoderFactory() {
    }

    public static PasswordEncoder create(PasswordEncoderType type) {
        if (type == null) {
            throw new IllegalArgumentException("Password encoder type must not be null");
        }

        return switch (type) {
            case BCRYPT -> new BCryptPasswordEncoder();
        };
    }
}
