package org.fitory.auth.service;

import org.fitory.auth.dto.LoginRequest;
import org.fitory.auth.dto.LoginResponse;
import org.fitory.auth.dto.RefreshRequest;
import org.fitory.auth.dto.TokenResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    TokenResponse refresh(String refreshToken);
    void logout(String refreshToken);
}
