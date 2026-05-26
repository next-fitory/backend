package org.fitory.auth.service.impl;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.auth.domain.RefreshToken;
import org.fitory.auth.dto.LoginRequest;
import org.fitory.auth.dto.LoginResponse;
import org.fitory.auth.dto.TokenResponse;
import org.fitory.auth.repository.RefreshTokenRepository;
import org.fitory.auth.service.AuthService;
import org.fitory.auth.service.JwtProvider;
import org.fitory.user.domain.User;
import org.fitory.user.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!BCrypt.checkpw(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail());
        String rawRefreshToken = issueRefreshToken(user.getId());
        return new LoginResponse(user.getId(), accessToken, rawRefreshToken);
    }

    @Override
    public TokenResponse refresh(String rawRefreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        if (stored.isExpired()) {
            refreshTokenRepository.deleteByToken(stored.getToken());
            throw new IllegalArgumentException("Refresh token expired");
        }
        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        refreshTokenRepository.deleteByToken(stored.getToken());
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail());
        String newRawRefreshToken = issueRefreshToken(user.getId());
        return new TokenResponse(accessToken, newRawRefreshToken);
    }

    @Override
    public void logout(String rawRefreshToken) {
        refreshTokenRepository.deleteByToken(rawRefreshToken);
    }

    private String issueRefreshToken(Long userId) {
        String token = jwtProvider.generateRefreshToken();
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiresAt(now.plusSeconds(jwtProvider.getRefreshTokenExpirySeconds()))
                .createdAt(now)
                .build());
        return token;
    }
}
