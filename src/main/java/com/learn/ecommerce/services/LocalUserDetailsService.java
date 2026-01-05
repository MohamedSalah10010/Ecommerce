package com.learn.ecommerce.services;

import com.learn.ecommerce.repository.LocalUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalUserDetailsService implements UserDetailsService {

    private final LocalUserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return userRepo.findByUserNameIgnoreCase(username)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
    }
}
