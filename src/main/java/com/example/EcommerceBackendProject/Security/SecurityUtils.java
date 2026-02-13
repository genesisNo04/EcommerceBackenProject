package com.example.EcommerceBackendProject.Security;

import com.example.EcommerceBackendProject.Entity.CustomUserDetails;
import com.example.EcommerceBackendProject.Exception.UserAccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public SecurityUtils() {}

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    private CustomUserDetails getPrincipal() {
        Authentication authentication = getAuthentication();

        if (authentication == null) {
            throw new UserAccessDeniedException("Unauthenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails;
        }

        throw new UserAccessDeniedException("Unauthenticated");
    }

    public CustomUserDetails getCurrentUser() {
        return getPrincipal();
    }

    public Long getCurrentUserId() {
        return getPrincipal().getId();
    }

    public String getCurrentUsername() {
        return getPrincipal().getUsername();
    }

    public String getCurrentEmail() {
        return getPrincipal().getEmail();
    }

    public void requireAdmin() {
        if (!isAdmin()) {
            throw new UserAccessDeniedException("Admin privileges required");
        }
    }

    public boolean isAdmin() {
        getPrincipal();
        return getAuthentication().getAuthorities().stream().
                anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
