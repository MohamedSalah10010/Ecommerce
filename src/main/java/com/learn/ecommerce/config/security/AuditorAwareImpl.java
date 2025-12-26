package com.learn.ecommerce.config.security;

import com.learn.ecommerce.entity.LocalUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                return Optional.of(((UserDetails) principal).getUsername());
            } else if (principal instanceof LocalUser) { // if you are returning your entity as principal
                return Optional.of(((LocalUser) principal).getUserName());
            } else {
                return Optional.of(principal.toString());
            }
        }

        // Fallback for unauthenticated users (e.g., system actions or registration)
        return Optional.of("SYSTEM");
    }
}
