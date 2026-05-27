package org.fitory.user.service.impl;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.auth.domain.User;
import org.fitory.user.dto.UserResponse;
import org.fitory.user.exception.UserNotFoundException;
import org.fitory.user.repository.UserRepository;
import org.fitory.user.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse findByEmail(String email) {
        Optional<User> find = findByOptEmail(email);
        return UserResponse.of(find.orElseThrow(() -> new UserNotFoundException(email)));
    }


    @Override
    public Optional<User> findByOptEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByOptId(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Map<Long, UserResponse> findAllByIds(List<Long> ids) {
        return userRepository.findAllByIds(ids).stream()
                .collect(Collectors.toMap(User::getId, UserResponse::of));
    }

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

    private User findDomain(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
