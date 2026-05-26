package org.fitory.review.repository;

import org.fitory.review.domain.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review save(Review review);
    Optional<Review> findById(Long id);
    List<Review> findAll(int page, int size);
    long count();
    List<Review> findAllByProductId(Long productId, int page, int size);
    long countByProductId(Long productId);
    void deleteById(Long id);
}
