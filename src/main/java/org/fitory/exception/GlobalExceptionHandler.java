package org.fitory.exception;

import mvc.ResponseEntity;
import mvc.annotation.ControllerAdvice;
import mvc.annotation.ExceptionHandler;
import org.fitory.brand.exception.BrandNotFoundException;
import org.fitory.cart.exception.CartItemAccessDeniedException;
import org.fitory.cart.exception.CartItemNotFoundException;
import org.fitory.example.ProductNotFoundException;
import org.fitory.review.exception.ReviewNotFoundException;
import org.fitory.user.exception.UserNotFoundException;
import org.fitory.userlike.exception.UserLikeNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException e) {
        return ResponseEntity.notFound(ErrorResponse.of(ErrorCode.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(BrandNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBrandNotFound(BrandNotFoundException e) {
        return ResponseEntity.notFound(ErrorResponse.of(ErrorCode.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartItemNotFound(CartItemNotFoundException e) {
        return ResponseEntity.notFound(ErrorResponse.of(ErrorCode.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(CartItemAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleCartItemAccessDenied(CartItemAccessDeniedException e) {
        return ResponseEntity.forbidden(ErrorResponse.of(ErrorCode.FORBIDDEN, e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.notFound(ErrorResponse.of(ErrorCode.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReviewNotFound(ReviewNotFoundException e) {
        return ResponseEntity.notFound(ErrorResponse.of(ErrorCode.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(UserLikeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserLikeNotFound(UserLikeNotFoundException e) {
        return ResponseEntity.notFound(ErrorResponse.of(ErrorCode.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest(ErrorResponse.of(ErrorCode.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        return ResponseEntity.internalServerError(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
