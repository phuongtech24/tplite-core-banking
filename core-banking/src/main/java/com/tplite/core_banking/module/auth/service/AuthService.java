package com.tplite.core_banking.module.auth.service;

import com.tplite.core_banking.module.auth.dto.AuthResponse;
import com.tplite.core_banking.module.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
}
