package org.fitory.userlike.repository;

import org.fitory.userlike.domain.UserLike;

import java.util.List;
import java.util.Optional;

public interface UserLikeRepository {
    UserLike save(UserLike userLike);
    Optional<UserLike> findById(Long id);
    Optional<UserLike> findByUserIdAndProductId(Long userId, Long productId);
    List<UserLike> findAllByUserId(Long userId);
    List<UserLike> findAllByProductId(Long productId);
    void deleteById(Long id);
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
