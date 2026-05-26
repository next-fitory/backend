package org.fitory.user.service;

import org.fitory.user.dto.CreateUserRequest;
import org.fitory.user.dto.UpdateUserRequest;
import org.fitory.user.dto.UserResponse;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    Map<Long, UserResponse> findAllByIds(List<Long> ids);
    UserResponse create(CreateUserRequest request);
    UserResponse update(Long id, UpdateUserRequest request);
    void delete(Long id);
}
