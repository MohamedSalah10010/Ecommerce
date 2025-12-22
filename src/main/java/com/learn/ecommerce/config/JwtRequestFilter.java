package com.learn.ecommerce.config;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private LocalUserRepo  localUserRepo;

    public JwtRequestFilter(JwtService jwtService, LocalUserRepo  localUserRepo) {
        this.jwtService = jwtService;
        this.localUserRepo = localUserRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterchain)
            throws ServletException, IOException
    {
        String path = request.getServletPath();

        // Skip JWT filter for login & register
        if ("/auth/login".equals(path) || "/auth/register".equals(path) || path.startsWith("/auth/verify") || path.startsWith("/auth/forgot-password") || path.startsWith("/auth/reset-password")) {
            filterchain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")){
            String token = header.substring(7);
            try
            {
                String username = jwtService.getUsername(token);
                Optional<LocalUser> opUser = localUserRepo.findByUserNameIgnoreCase(username);
                if(opUser.isPresent())
                {
                    LocalUser user = opUser.get();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }


            }catch (JWTDecodeException jwtDecodeException){

            }
        }

        filterchain.doFilter(request, response);



    }


}
