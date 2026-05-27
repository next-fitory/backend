package org.fitory.auth.domain;

public enum Role {
    USER,
    ADMIN;

    public Role getRole(String role) {
        if (role == null) return null;
        return Role.valueOf(role.toUpperCase());
    }
}
