package org.fitory.exception;

import mvc.HttpStatus;
import mvc.ResponseEntity;
import mvc.annotation.ControllerAdvice;
import mvc.annotation.ExceptionHandler;
import org.fitory.product.ProductNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ProductNotFoundException e) {
        return ResponseEntity.notFound(ErrorResponse.of("NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest(ErrorResponse.of("BAD_REQUEST", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        return ResponseEntity.internalServerError(
                ErrorResponse.of("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
    }
}
