package org.fitory.review.service;

import org.fitory.common.dto.PageResponse;
import org.fitory.review.dto.CreateReviewRequest;
import org.fitory.review.dto.ReviewResponse;
import org.fitory.review.dto.UpdateReviewRequest;

public interface ReviewService {
    PageResponse<ReviewResponse> findAll(int page, int size);
    PageResponse<ReviewResponse> findAllByProductId(Long productId, int page, int size);
    ReviewResponse findById(Long id);
    ReviewResponse create(Long userId, CreateReviewRequest request);
    ReviewResponse update(Long id, Long userId, UpdateReviewRequest request);
    void delete(Long id, Long userId);
}
