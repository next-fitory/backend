package org.fitory.user.service.impl;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.user.domain.User;
import org.mindrot.jbcrypt.BCrypt;
import org.fitory.user.dto.CreateUserRequest;
import org.fitory.user.dto.UpdateUserRequest;
import org.fitory.user.dto.UserResponse;
import org.fitory.user.exception.UserNotFoundException;
import org.fitory.user.repository.UserRepository;
import org.fitory.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::of)
                .toList();
    }

    @Override
    public UserResponse findById(Long id) {
        return UserResponse.of(findDomain(id));
    }

    @Override
    public Map<Long, UserResponse> findAllByIds(List<Long> ids) {
        return userRepository.findAllByIds(ids).stream()
                .collect(Collectors.toMap(User::getId, UserResponse::of));
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        LocalDateTime now = LocalDateTime.now();
        return UserResponse.of(userRepository.save(User.builder()
                .email(request.email())
                .name(request.name())
                .password(BCrypt.hashpw(request.password(), BCrypt.gensalt()))
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
                .build()));
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        User existing = findDomain(id);
        return UserResponse.of(userRepository.save(existing.toBuilder()
                .email(request.email() != null ? request.email() : existing.getEmail())
                .name(request.name() != null ? request.name() : existing.getName())
                .updatedAt(LocalDateTime.now())
                .build()));
    }

    @Override
    public void delete(Long id) {
        findDomain(id);
        userRepository.deleteById(id);
    }

    private User findDomain(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
