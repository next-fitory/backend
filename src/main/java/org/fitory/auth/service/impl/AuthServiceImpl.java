package org.fitory.auth.service.impl;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.auth.domain.Role;
import org.fitory.auth.domain.User;
import org.fitory.auth.dto.LoginRequest;
import org.fitory.auth.dto.SignupRequest;
import org.fitory.auth.dto.TokenResponse;
import org.fitory.auth.repository.UserRepository;
import org.fitory.auth.service.AuthService;
import org.fitory.auth.service.NicknameGenerator;
import org.fitory.security.JwtProvider;
import org.fitory.security.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final NicknameGenerator nicknameGenerator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User signup(SignupRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .name(nicknameGenerator.generate())
                .build();

        return userRepository.save(user);
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(user);
        return new TokenResponse(accessToken);
    }
}