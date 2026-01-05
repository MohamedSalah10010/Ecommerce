package com.learn.ecommerce.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/test" )
    public String testEndpoint() {
        return "Test successful";
    }
    @GetMapping("/debug")
    public Object debugAuth(Authentication authentication) {
        return authentication.getAuthorities();
    }
    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth: " + auth);
        System.out.println("Authorities: " + auth.getAuthorities());
        System.out.println("Principal: " + auth.getPrincipal());
        return ResponseEntity.ok("Check console");
    }
}
