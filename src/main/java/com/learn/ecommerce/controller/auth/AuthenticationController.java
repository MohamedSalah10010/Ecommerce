package com.learn.ecommerce.controller.auth;

import com.learn.ecommerce.DTO.ErrorResponseDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.*;
import com.learn.ecommerce.DTO.UserResponseDTO.LoginResponseDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.LogoutResponseDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserStatusDTO;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.services.JwtService;
import com.learn.ecommerce.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j

@RestController
@RequestMapping("/auth")

@Tag(
        name = "Authentication",
        description = "User registration, login, email verification, and password management"
)
public class AuthenticationController {

    private UserService userService;
    private JwtService jwtService;
    private LocalUserRepo localUserRepo;

    public AuthenticationController(JwtService jwtService, LocalUserRepo localUserRepo, UserService userService) {
        this.jwtService = jwtService;
        this.localUserRepo = localUserRepo;
        this.userService = userService;
    }


    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and sends a verification email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Email service failure",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegistrationBodyDTO registrationBodyDTO)
    {

            return new ResponseEntity<>(userService.registerUser(registrationBodyDTO), HttpStatus.CREATED);

    }


    @Operation(
            summary = "User login",
            description = "Authenticates user credentials and returns a JWT access token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User email not verified",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginBodyDTO loginBodyDTO)
    {

        return new ResponseEntity<LoginResponseDTO>(userService.loginUser(loginBodyDTO), HttpStatus.OK);

    }


    @Operation(
            summary = "Get authenticated user profile",
            description = "Returns profile information of the currently logged-in user"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user)
    {
        return new ResponseEntity<>( userService.getUserProfile(user), HttpStatus.OK);
    }


    @Operation(
            summary = "Verify user email",
            description = "Verifies user email using a verification token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token expired",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Token or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/verify")
    public ResponseEntity<UserStatusDTO> verifyUserEmail(@RequestParam("token") String token)
    {
        return new ResponseEntity<UserStatusDTO>( userService.verifyUserEmail(token),HttpStatus.OK);

    }


    @Operation(
            summary = "Initiate password reset",
            description = "Sends password reset instructions to the user's email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reset email sent"),
            @ApiResponse(
                    responseCode = "403",
                    description = "User email not verified",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@Valid @RequestBody ForgetPasswordBodyDTO forgetPasswordBodyDTO)
    {

            userService.initiatePasswordReset(forgetPasswordBodyDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Password reset instructions have been sent to your email.");

    }


    @Operation(
            summary = "Reset user password",
            description = "Resets the user's password using a valid reset token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token expired",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Token or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping ("/reset-password")
    public ResponseEntity<UserStatusDTO> resetPassword(@Valid @RequestBody ResetPasswordBodyDTO resetPasswordBodyDTO)
    {
        return new ResponseEntity<UserStatusDTO>( userService.resetPassword(resetPasswordBodyDTO.getNewPassword(), resetPasswordBodyDTO.getToken()),HttpStatus.OK);

    }

    @PostMapping("/request-verify")
    public ResponseEntity  requestEmailVerification(@Valid @RequestBody RequestEmailVerificationDTO body)
    {
        userService.requestEmailVerification(body);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/update/{userId}")
    public  ResponseEntity<UserStatusDTO> updateUser(@PathVariable Long userId,@Valid @RequestBody EditUserBody body)
    {
        return new ResponseEntity<UserStatusDTO>( userService.updateUserProfile(userId, body), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logoutUser(@AuthenticationPrincipal LocalUser user)
    {
        return new ResponseEntity<LogoutResponseDTO>( userService.logoutUser(user), HttpStatus.OK);
    }
}