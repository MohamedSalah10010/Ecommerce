package com.learn.ecommerce.services;

import com.learn.ecommerce.repository.LocalUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalUserDetailsService implements UserDetailsService {

    private final LocalUserRepo userRepo;

    @Override
    public @NotNull UserDetails loadUserByUsername(@NotNull String username)
            throws UsernameNotFoundException {
        return userRepo.findByUserNameIgnoreCase(username)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> {
                    log.warn("Authentication failed: username={} not found or deleted", username);
                    return new UsernameNotFoundException("User not found");
                });
    }
}