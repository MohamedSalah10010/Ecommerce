package com.learn.ecommerce.controller.auth;

import com.learn.ecommerce.DTO.ErrorResponseDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.*;
import com.learn.ecommerce.DTO.UserResponseDTO.LoginResponseDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.LogoutResponseDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserStatusDTO;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.exceptionhandler.UserNotFoundException;
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
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(
        name = "Authentication",
        description = "User registration, login, email verification, and password management"
)
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;
    private final LocalUserRepo localUserRepo;

    public AuthenticationController(JwtService jwtService, LocalUserRepo localUserRepo, UserService userService) {
        this.jwtService = jwtService;
        this.localUserRepo = localUserRepo;
        this.userService = userService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account and sends a verification email")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Email service failure",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/register")
    @RolesAllowed({"ADMIN"}) // Only ADMIN can register users
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegistrationBodyDTO registrationBodyDTO) {
        log.info("Admin registering new user with username: {}", registrationBodyDTO.getUsername());
        UserDTO registeredUser = userService.registerUser(registrationBodyDTO);
        log.info("User registered successfully: {}", registeredUser.getUserName());
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @Operation(summary = "User login", description = "Authenticates user credentials and returns a JWT access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "User email not verified",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginBodyDTO loginBodyDTO) {
        log.info("Attempting login for username: {}", loginBodyDTO.getUsername());
        LoginResponseDTO loginResponse = userService.loginUser(loginBodyDTO);
        log.info("Login successful for username: {}", loginBodyDTO.getUsername());
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Get authenticated user profile", description = "Returns profile information of the currently logged-in user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/me")
    @RolesAllowed({"USER", "ADMIN"}) // Both roles can access
    public ResponseEntity<UserDTO> getLoggedInUserProfile(@AuthenticationPrincipal User userDetails) {
        log.info("Fetching profile for user: {}", userDetails.getUsername());
        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);
        UserDTO profile = userService.getUserProfile(user);
        log.info("Profile fetched for user: {}", user.getUsername());
        return ResponseEntity.ok(profile);
    }

    @Operation(summary = "Verify user email", description = "Verifies user email using a verification token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "401", description = "Token expired",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Token or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/verify")
    public ResponseEntity<UserStatusDTO> verifyUserEmail(@RequestParam("token") String token) {
        log.info("Verifying email with token: {}", token);
        UserStatusDTO status = userService.verifyUserEmail(token);
        log.info("Email verification completed with token: {}", token);
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Initiate password reset", description = "Sends password reset instructions to the user's email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reset email sent"),
            @ApiResponse(responseCode = "403", description = "User email not verified",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/forgot-password")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgetPasswordBodyDTO body) {
        log.info("Initiating password reset for email: {}", body.getEmail());
        userService.initiatePasswordReset(body);
        log.info("Password reset email sent to: {}", body.getEmail());
        return ResponseEntity.ok("Password reset instructions have been sent to your email.");
    }

    @Operation(summary = "Reset user password", description = "Resets the user's password using a valid reset token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset successful"),
            @ApiResponse(responseCode = "401", description = "Token expired",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Token or user not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/reset-password")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<UserStatusDTO> resetPassword(@Valid @RequestBody ResetPasswordBodyDTO body) {
        log.info("Resetting password with token: {}", body.getToken());
        UserStatusDTO status = userService.resetPassword(body.getNewPassword(), body.getToken());
        log.info("Password reset completed for token: {}", body.getToken());
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Request email verification", description = "Sends a verification email to the user")
    @PostMapping("/request-verify")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Void> requestEmailVerification(@Valid @RequestBody RequestEmailVerificationDTO body) {
        log.info("Requesting email verification for: {}", body.getEmail());
        userService.requestEmailVerification(body);
        log.info("Verification email requested for: {}", body.getEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update user profile", description = "Updates user information by user ID")
    @PutMapping("/update/{userId}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<UserStatusDTO> updateUser(@PathVariable Long userId, @Valid @RequestBody EditUserBody body) {
        log.info("Updating user profile for userId: {}", userId);
        UserStatusDTO updated = userService.updateUserProfile(userId, body);
        log.info("User profile updated for userId: {}", userId);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Logout user", description = "Logs out the currently authenticated user")
    @PostMapping("/logout")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<LogoutResponseDTO> logoutUser(@AuthenticationPrincipal User userDetails) {
        log.info("Logging out user: {}", userDetails.getUsername());
        LocalUser user = localUserRepo.findByUserNameIgnoreCase(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);
        LogoutResponseDTO logoutResponse = userService.logoutUser(user);
        log.info("User logged out: {}", user.getUsername());
        return ResponseEntity.ok(logoutResponse);
    }
}
