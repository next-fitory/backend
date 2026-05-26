package org.fitory.userlike.service.impl;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.userlike.domain.UserLike;
import org.fitory.userlike.dto.CreateUserLikeRequest;
import org.fitory.userlike.dto.UserLikeResponse;
import org.fitory.userlike.repository.UserLikeRepository;
import org.fitory.userlike.service.UserLikeService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLikeServiceImpl implements UserLikeService {
    private final UserLikeRepository userLikeRepository;

    @Override
    public List<UserLikeResponse> findAllByUserId(Long userId) {
        return userLikeRepository.findAllByUserId(userId).stream()
                .map(UserLikeResponse::of)
                .toList();
    }

    @Override
    public List<UserLikeResponse> findAllByProductId(Long productId) {
        return userLikeRepository.findAllByProductId(productId).stream()
                .map(UserLikeResponse::of)
                .toList();
    }

    @Override
    public UserLikeResponse like(CreateUserLikeRequest request) {
        userLikeRepository.findByUserIdAndProductId(request.userId(), request.productId())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Already liked this product");
                });
        UserLike saved = userLikeRepository.save(UserLike.builder()
                .userId(request.userId())
                .productId(request.productId())
                .createdAt(LocalDateTime.now())
                .build());
        return UserLikeResponse.of(saved);
    }

    @Override
    public void unlike(Long userId, Long productId) {
        userLikeRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Like not found"));
        userLikeRepository.deleteByUserIdAndProductId(userId, productId);
    }
}
