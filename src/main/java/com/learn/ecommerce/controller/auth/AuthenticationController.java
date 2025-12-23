package com.learn.ecommerce.controller.auth;

import com.learn.ecommerce.DTO.UserRequestDTO.ForgetPasswordBodyDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.LoginBodyDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.RegistrationBodyDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.ResetPasswordBodyDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.LoginResponseDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserStatusDTO;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.services.JwtService;
import com.learn.ecommerce.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private UserService userService;
    private JwtService jwtService;
    private LocalUserRepo localUserRepo;

    public AuthenticationController(JwtService jwtService, LocalUserRepo localUserRepo, UserService userService) {
        this.jwtService = jwtService;
        this.localUserRepo = localUserRepo;
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegistrationBodyDTO registrationBodyDTO)
    {

            return new ResponseEntity<>(userService.registerUser(registrationBodyDTO), HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginBodyDTO loginBodyDTO)
    {

        return new ResponseEntity<LoginResponseDTO>(userService.loginUser(loginBodyDTO), HttpStatus.OK);

    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user)
    {
        return new ResponseEntity<>( userService.getUserProfile(user), HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<UserStatusDTO> verifyUserEmail(@RequestParam("token") String token)
    {
        return new ResponseEntity<UserStatusDTO>( userService.verifyUserEmail(token),HttpStatus.OK);

    }

    @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@Valid @RequestBody ForgetPasswordBodyDTO forgetPasswordBodyDTO)
    {

            userService.initiatePasswordReset(forgetPasswordBodyDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Password reset instructions have been sent to your email.");

    }

    @PostMapping ("/reset-password")
    public ResponseEntity<UserStatusDTO> resetPassword(@Valid @RequestBody ResetPasswordBodyDTO resetPasswordBodyDTO)
    {
        return new ResponseEntity<UserStatusDTO>( userService.resetPassword(resetPasswordBodyDTO.getNewPassword(), resetPasswordBodyDTO.getToken()),HttpStatus.OK);

    }
}