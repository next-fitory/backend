package org.fitory.review.service.impl;

import core.annotation.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.common.dto.PageResponse;
import org.fitory.review.domain.Review;
import org.fitory.review.dto.CreateReviewRequest;
import org.fitory.review.dto.ReviewResponse;
import org.fitory.review.dto.UpdateReviewRequest;
import org.fitory.review.exception.ReviewNotFoundException;
import org.fitory.review.repository.ReviewRepository;
import org.fitory.review.service.ReviewService;
import org.fitory.user.dto.UserResponse;
import org.fitory.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    @Override
    public PageResponse<ReviewResponse> findAll(int page, int size) {
        List<Review> reviews = reviewRepository.findAll(page, size);
        long total = reviewRepository.count();
        return toPageResponse(reviews, page, size, total);
    }

    @Override
    public PageResponse<ReviewResponse> findAllByProductId(Long productId, int page, int size) {
        List<Review> reviews = reviewRepository.findAllByProductId(productId, page, size);
        long total = reviewRepository.countByProductId(productId);
        return toPageResponse(reviews, page, size, total);
    }

    @Override
    public ReviewResponse findById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        UserResponse user = userService.findById(review.getUserId());
        return ReviewResponse.of(review, user);
    }

    @Override
    public ReviewResponse create(Long userId, CreateReviewRequest request) {
        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("Content must not be blank");
        }
        if (request.rating() < 1 || request.rating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        UserResponse user = userService.findById(userId);
        LocalDateTime now = LocalDateTime.now();
        Review saved = reviewRepository.save(Review.builder()
                .productId(request.productId())
                .userId(userId)
                .content(request.content())
                .rating(request.rating())
                .createdAt(now)
                .updatedAt(now)
                .deleted(false)
                .build());
        return ReviewResponse.of(saved, user);
    }

    @Override
    public ReviewResponse update(Long id, UpdateReviewRequest request) {
        if (request.rating() < 1 || request.rating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        UserResponse user = userService.findById(existing.getUserId());
        Review updated = reviewRepository.save(existing.toBuilder()
                .content(request.content() != null ? request.content() : existing.getContent())
                .rating(request.rating() != 0 ? request.rating() : existing.getRating())
                .updatedAt(LocalDateTime.now())
                .build());
        return ReviewResponse.of(updated, user);
    }

    @Override
    public void delete(Long id) {
        reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        reviewRepository.deleteById(id);
    }

    private PageResponse<ReviewResponse> toPageResponse(List<Review> reviews, int page, int size, long total) {
        List<Long> userIds = reviews.stream().map(Review::getUserId).distinct().toList();
        Map<Long, UserResponse> userMap = userService.findAllByIds(userIds);
        List<ReviewResponse> content = reviews.stream()
                .map(r -> ReviewResponse.of(r, userMap.get(r.getUserId())))
                .collect(Collectors.toList());
        return PageResponse.of(content, page, size, total);
    }
}
