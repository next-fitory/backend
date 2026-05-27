package org.fitory.userlike.service;

import org.fitory.userlike.dto.CreateUserLikeRequest;
import org.fitory.userlike.dto.UserLikeResponse;

import java.util.List;

public interface UserLikeService {
    List<UserLikeResponse> findAllByUserId(Long userId);
    List<UserLikeResponse> findAllByProductId(Long productId);
    UserLikeResponse like(CreateUserLikeRequest request);
    void unlike(Long userId, Long productId);
}
