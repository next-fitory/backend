package org.fitory.userlike.exception;

public class UserLikeNotFoundException extends RuntimeException {
    public UserLikeNotFoundException(Long id) {
        super("UserLike not found with id: " + id);
    }
}
