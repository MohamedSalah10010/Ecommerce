package com.learn.ecommerce.controller.auth;

import com.learn.ecommerce.DTO.UserRequestDTO.ForgetPasswordBodyDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.LoginBodyDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.RegistrationBodyDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.ResetPasswordBodyDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.LoginResponseDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserDTO;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.exceptionhandler.EmailFailureException;
import com.learn.ecommerce.exceptionhandler.UserIsNotVerifiedException;
import com.learn.ecommerce.exceptionhandler.UserNotFound;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.services.JwtService;
import com.learn.ecommerce.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
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
    public UserDTO getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user)
    {
        return userService.getUserProfile(user);
    }

    @GetMapping("/verify")
    public ResponseEntity verifyUserEmail(@RequestParam("token") String token)
    {
        boolean verified = userService.verifyUserEmail(token);
        if(verified) {
            return ResponseEntity.ok().body("Account is verified successfully!!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@Valid @RequestBody ForgetPasswordBodyDTO forgetPasswordBodyDTO)
    {
        try
        {
            userService.initiatePasswordReset(forgetPasswordBodyDTO);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch (UserNotFound ex)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        catch (EmailFailureException ex)
        {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        catch (UserIsNotVerifiedException ex)
        {
            if (!ex.isEmailSent())
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification email sent to your registered email address.");
        }


    }

    @PostMapping ("/reset-password")
    public ResponseEntity resetPassword(@Valid @RequestBody ResetPasswordBodyDTO resetPasswordBodyDTO)
    {
        boolean reset = userService.resetPassword(resetPasswordBodyDTO.getNewPassword(), resetPasswordBodyDTO.getToken());
        if (reset) {
            return ResponseEntity.ok().body("Password has been reset successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired password reset token.");
        }
    }
}