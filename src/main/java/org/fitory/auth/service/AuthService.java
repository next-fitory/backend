package org.fitory.auth.service;

import org.fitory.auth.domain.User;
import org.fitory.auth.dto.LoginRequest;
import org.fitory.auth.dto.SignupRequest;
import org.fitory.auth.dto.TokenResponse;

public interface AuthService {
    User signup(SignupRequest request);
    TokenResponse login(LoginRequest request);
}