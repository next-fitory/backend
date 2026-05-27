package org.fitory.user.repository;

import org.fitory.auth.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    List<User> findAllByIds(List<Long> ids);

    List<User> findAll();
}
