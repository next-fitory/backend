package org.fitory.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND("NOT_FOUND", "Resource not found"),
    BAD_REQUEST("BAD_REQUEST", "Invalid request"),
    ALREADY_EXISTS("ALREADY_EXISTS", "Resource already exists"),
    FORBIDDEN("FORBIDDEN", "Access denied"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An unexpected error occurred");

    private final String code;
    private final String message;
}
