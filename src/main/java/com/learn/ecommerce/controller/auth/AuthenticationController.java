package com.learn.ecommerce.controller.auth;

import com.learn.ecommerce.DTO.UserRequestDTO.ForgetPasswordBodyDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.LoginBodyDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.RegistrationBodyDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.ResetPasswordBodyDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.LoginResponseDTO;
import com.learn.ecommerce.exceptionhandler.EmailFailureException;
import com.learn.ecommerce.exceptionhandler.UserAlreadyExistsException;
import com.learn.ecommerce.exceptionhandler.UserIsNotVerifiedException;
import com.learn.ecommerce.exceptionhandler.UserNotFound;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.services.JwtService;
import com.learn.ecommerce.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBodyDTO registrationBodyDTO)
    {

        try {
            userService.registerUser(registrationBodyDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }catch (EmailFailureException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginBodyDTO loginBodyDTO)
    {
        String jwt = null;
        try {
             jwt = userService.loginUser(loginBodyDTO);
        }catch (UserIsNotVerifiedException ex)
        {
            LoginResponseDTO loginResponse = new LoginResponseDTO();
            String reason = "Email is not verified. Please verify your email to login.";
            if(ex.isEmailSent())
            {
                reason += " A new verification email has been sent to your email address.";
            }
            else{
                reason += " Failed to send verification email. Please try resending verification email.";
            }
            loginResponse.setFailureMessage(reason);
            loginResponse.setSuccess(false);
            loginResponse.setJwt(null);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(loginResponse);

        }
        catch (EmailFailureException ex)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (jwt == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        else{
        LoginResponseDTO response = new LoginResponseDTO();
        response.setJwt(jwt);
        response.setSuccess(true);
        return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/me")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user)
    {
        return user;
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