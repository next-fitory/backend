package org.fitory.user.service;

import org.fitory.auth.domain.User;
import org.fitory.user.dto.UserResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    UserResponse findByEmail(String email);

    List<UserResponse> findAll();

    UserResponse findById(Long id);

    Optional<User> findByOptEmail(String email);

    Optional<User> findByOptId(Long id);

    Map<Long, UserResponse> findAllByIds(List<Long> ids);
}
