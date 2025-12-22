package com.learn.ecommerce.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.learn.ecommerce.entity.LocalUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
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
    }

    public String generateToken(LocalUser user) {
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getUserName())
                .withIssuer(issuer)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(expiryInSeconds)))
                .sign(algorithm);
    }

    public String generateVerificationToken(LocalUser user) {
        return JWT.create()
                .withClaim(VERIFICATION_EMAIL_KEY, user.getEmail())
                .withIssuer(issuer)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(24 * 60 * 60))) // 24 hours
                .sign(algorithm);
    }

    public String generatePasswordResetToken(LocalUser user) {
        return JWT.create()
                .withClaim(PASSWORD_RESET_EMAIL_KEY, user.getEmail())
                .withIssuer(issuer)
                .withExpiresAt(Date.from(Instant.now().plusSeconds( 60 * 30))) // 30 min
                .sign(algorithm);
    }

    public String getUsername(String token) {

        DecodedJWT jwtDecoded = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return jwtDecoded .getClaim(USERNAME_KEY).asString();
    }
    public String getVerificationEmail(String token) {

        DecodedJWT jwtDecoded = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return jwtDecoded.getClaim(VERIFICATION_EMAIL_KEY).asString();
    }

    public String getPasswordResetEmail(String token) {

        DecodedJWT jwtDecoded = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return jwtDecoded.getClaim(PASSWORD_RESET_EMAIL_KEY).asString();
    }
//public String getPasswordResetEmail(String token) {
//    if (token == null || token.isBlank()) throw new IllegalArgumentException("JWT token is empty");
//
//    // remove quotes/spaces, decode URL
//    token = token.replace("\"", "").trim();
//    token = URLDecoder.decode(token, StandardCharsets.UTF_8);
//
//    DecodedJWT jwtDecoded = JWT.require(algorithm)
//            .withIssuer(issuer)
//            .build()
//            .verify(token);
//
//    return jwtDecoded.getClaim(PASSWORD_RESET_EMAIL_KEY).asString();
//}

}
