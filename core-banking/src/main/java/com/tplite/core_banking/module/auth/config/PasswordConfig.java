package com.tplite.core_banking.module.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tplite.core_banking.module.auth.factory.PasswordEncoderFactory;
import com.tplite.core_banking.module.auth.factory.PasswordEncoderType;

@Configuration
public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder(
            @Value("${app.security.password.encoder:BCRYPT}") PasswordEncoderType encoderType
    ) {
        return PasswordEncoderFactory.create(encoderType);
    }
}
