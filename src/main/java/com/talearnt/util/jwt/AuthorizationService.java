package com.talearnt.util.jwt;


import com.talearnt.enums.user.UserRole;
import org.springframework.stereotype.Component;

@Component("authz")
public class AuthorizationService {
    public boolean admin(UserInfo user) {
        return hasRole(user, UserRole.ROLE_ADMIN);
    }

    public boolean manager(UserInfo user) {
        return hasRole(user, UserRole.ROLE_MANAGER);
    }

    public boolean user(UserInfo user) {
        return hasRole(user, UserRole.ROLE_USER);
    }

    private boolean hasRole(UserInfo user, UserRole requiredRole) {
        return user != null
                && user.isActiveUser()
                && user.hasHigherOrEqual(requiredRole);
    }
}
