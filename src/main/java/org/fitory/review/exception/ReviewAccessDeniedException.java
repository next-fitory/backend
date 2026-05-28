package org.fitory.review.exception;

public class ReviewAccessDeniedException extends RuntimeException {
    public ReviewAccessDeniedException(Long id) {
        super("Access denied to review with id: " + id);
    }
}
