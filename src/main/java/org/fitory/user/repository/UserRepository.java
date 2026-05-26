package org.fitory.user.repository;

import org.fitory.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAllByIds(List<Long> ids);
    List<User> findAll();
    void deleteById(Long id);
}
