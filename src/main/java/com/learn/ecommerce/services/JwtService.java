package com.learn.ecommerce.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.learn.ecommerce.entity.LocalUser;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
@Getter
public class JwtService {
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    private Algorithm algorithm;
    private static final String USERNAME_KEY = "USERNAME";
    private static final String VERIFICATION_EMAIL_KEY = "VERIFICATION_EMAIL";
    private static final String PASSWORD_RESET_EMAIL_KEY = "PASSWORD_RESET_EMAIL";

    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(algorithmKey);
        log.info("JWT Algorithm initialized successfully");
    }

    public String generateToken(LocalUser user) {
        String token = JWT.create()
                .withClaim(USERNAME_KEY, user.getUsername())
                .withClaim("ROLE", user.getUserRoles()
                        .stream()
                        .map(role -> role.getRoleName()).toList())
                .withIssuer(issuer)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(expiryInSeconds)))
                .sign(algorithm);
        log.debug("Generated JWT token for user: {}", user.getUsername());
        return token;
    }

    public String generateVerificationToken(LocalUser user) {
        String token = JWT.create()
                .withClaim(VERIFICATION_EMAIL_KEY, user.getEmail())
                .withIssuer(issuer)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(24 * 60 * 60))) // 24 hours
                .sign(algorithm);
        log.debug("Generated verification token for email: {}", user.getEmail());
        return token;
    }

    public String generatePasswordResetToken(LocalUser user) {
        String token = JWT.create()
                .withClaim(PASSWORD_RESET_EMAIL_KEY, user.getEmail())
                .withIssuer(issuer)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(60 * 30))) // 30 min
                .sign(algorithm);
        log.debug("Generated password reset token for email: {}", user.getEmail());
        return token;
    }

    public String getUsername(String token) {
        try {
            DecodedJWT jwtDecoded = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
            return jwtDecoded.getClaim(USERNAME_KEY).asString();
        } catch (Exception e) {
            log.warn("Failed to decode JWT username: {}", e.getMessage());
            throw e;
        }
    }

    public String getVerificationEmail(String token) {
        try {
            DecodedJWT jwtDecoded = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
            return jwtDecoded.getClaim(VERIFICATION_EMAIL_KEY).asString();
        } catch (Exception e) {
            log.warn("Failed to decode verification token: {}", e.getMessage());
            throw e;
        }
    }

    public String getPasswordResetEmail(String token) {
        try {
            DecodedJWT jwtDecoded = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
            return jwtDecoded.getClaim(PASSWORD_RESET_EMAIL_KEY).asString();
        } catch (Exception e) {
            log.warn("Failed to decode password reset token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiresAt = JWT.decode(token).getExpiresAt();
            boolean expired = expiresAt == null || expiresAt.before(new Date());
            if (expired) log.debug("Token is expired or has no expiry date");
            return expired;
        } catch (JWTDecodeException e) {
            log.warn("JWT decoding failed or token invalid: {}", e.getMessage());
            return true;
        }
    }
}
