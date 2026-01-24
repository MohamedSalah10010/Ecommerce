package com.learn.ecommerce.config.security;

import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.entity.LoginTokens;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.repository.LoginTokensRepo;
import com.learn.ecommerce.services.JwtService;
import com.learn.ecommerce.services.LocalUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;


@Component

public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final LocalUserRepo localUserRepo;
    private final LoginTokensRepo loginTokensRepo;
    private final UserDetailsService userDetailsService;

    private final LocalUserDetailsService localUserDetailsService;

    public JwtRequestFilter(
            JwtService jwtService,
            LocalUserRepo localUserRepo,
            LoginTokensRepo loginTokensRepo,
            UserDetailsService userDetailsService, LocalUserDetailsService localUserDetailsService) {
        this.jwtService = jwtService;
        this.localUserRepo = localUserRepo;
        this.loginTokensRepo = loginTokensRepo;
        this.userDetailsService = userDetailsService;
        this.localUserDetailsService = localUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
		    HttpServletRequest request,
		    @NotNull HttpServletResponse response,
		    @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getServletPath();
        // Skip JWT filter for login & register
        if ( "/auth/login".equals(path)
                || path.startsWith("/auth/verify")
                || path.startsWith("/auth/forgot-password")
                || path.startsWith("/auth/reset-password")
                || "/products".equals(path)
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/auth/request-verify") )
        {
            filterChain.doFilter(request, response);
            return;
        }
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            String username = jwtService.getUsername(token);

            Optional<LocalUser> userOpt =
                    localUserRepo.findByUserNameIgnoreCase(username);
            if (userOpt.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            Optional<LoginTokens> tokenOpt =
                    loginTokensRepo.findByToken(token);
            if (tokenOpt.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            LoginTokens tokenEntity = tokenOpt.get();

            if (jwtService.isTokenExpired(token)) {
                tokenEntity.setExpired(true);
                loginTokensRepo.save(tokenEntity);
                filterChain.doFilter(request, response);
                return;
            }

            if (tokenEntity.getExpired() || tokenEntity.getRevoked()) {
                filterChain.doFilter(request, response);
                return;
            }

            LocalUser user = userOpt.get();

//            // Wrap LocalUser into Spring Security UserDetails
//            User userDetails = (User) User.builder()
//                    .username(user.getUsername())
//                    .password(user.getPassword())
//                    .authorities(user.getAuthorities())
//                    .accountLocked(!user.isAccountNonLocked())
//                    .disabled(!user.isEnabled())
//                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("Authorities: " + authentication.getAuthorities());
            System.out.println("Principal: " + authentication.getPrincipal());


            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            filterChain.doFilter(request, response);
        }
    }

}