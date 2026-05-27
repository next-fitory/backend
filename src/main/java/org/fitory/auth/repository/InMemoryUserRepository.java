package org.fitory.auth.repository;

import core.annotation.Repository;
import org.fitory.auth.domain.Role;
import org.fitory.auth.domain.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // 어드민 계정
    public InMemoryUserRepository() {
        User rootAdmin = User.builder()
                .id(idGenerator.getAndIncrement()) // ID: 1 부여
                .email("admin@fitory.com")
                .password("admin123")
                .role(Role.ADMIN)
                .build();

        store.put(rootAdmin.getId(), rootAdmin);
        System.out.println("[System] 어드민 계정이 생성되었습니다. (admin@fitory.com)");
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}