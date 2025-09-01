package com.talearnt.enums.user;


import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_WITHDRAWN(-1),
    ROLE_SUSPENDED(-1),
    ROLE_USER(1),
    ROLE_AUTHENTICATED(2),
    ROLE_MANAGER(3),
    ROLE_ADMIN(4);

    private final int level;

    UserRole(int level) {
        this.level = level;
    }

    public boolean isHigherOrEqual(UserRole requiredRole) {
        return this.level >= requiredRole.level;
    }

}
