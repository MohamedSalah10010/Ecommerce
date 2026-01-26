package com.learn.ecommerce.controller.auth;

import com.learn.ecommerce.DTO.ErrorResponseDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.*;
import com.learn.ecommerce.DTO.UserResponseDTO.LoginResponseDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.LogoutResponseDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserStatusDTO;
import com.learn.ecommerce.entity.LocalUser;
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
import org.jetbrains.annotations.NotNull;
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

    private final UserService userService;


    public AuthenticationController( UserService userService) {

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
    @RolesAllowed({"ADMIN","USER"})
    public ResponseEntity<@NotNull UserDTO> registerUser(@Valid @RequestBody RegistrationBodyDTO registrationBodyDTO) {

        UserDTO registeredUser = userService.registerUser(registrationBodyDTO);

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
    public ResponseEntity<@NotNull LoginResponseDTO> login(@Valid @RequestBody LoginBodyDTO loginBodyDTO) {
        LoginResponseDTO loginResponse = userService.loginUser(loginBodyDTO);

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
    public ResponseEntity<@NotNull UserDTO> getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {

	    UserDTO profile = userService.getUserProfile(user);

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
    public ResponseEntity<@NotNull UserStatusDTO> verifyUserEmail(@RequestParam("token") String token) {

        UserStatusDTO status = userService.verifyUserEmail(token);

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
    public ResponseEntity<@NotNull String> forgotPassword(@Valid @RequestBody ForgetPasswordBodyDTO body) {

        userService.initiatePasswordReset(body);

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
    public ResponseEntity<@NotNull UserStatusDTO> resetPassword(@Valid @RequestBody ResetPasswordBodyDTO body) {

        UserStatusDTO status = userService.resetPassword(body.getNewPassword(), body.getToken());

        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Request email verification", description = "Sends a verification email to the user")
    @PostMapping("/request-verify")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<@NotNull Void> requestEmailVerification(@Valid @RequestBody RequestEmailVerificationDTO body) {

        userService.requestEmailVerification(body);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update user profile", description = "Updates user information by user ID")
    @PutMapping("/update/{userId}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<@NotNull UserStatusDTO> updateUser(@PathVariable Long userId, @Valid @RequestBody EditUserBody body) {

        UserStatusDTO updated = userService.updateUserProfileById(userId, body);
        return ResponseEntity.ok(updated);
    }

	@Operation(summary = "Update user profile", description = "Updates user information for logged in user")
	@PutMapping("/update/logged-user")
	@RolesAllowed({"USER", "ADMIN"})
	public ResponseEntity<@NotNull UserStatusDTO> updateLoggedInUser(@AuthenticationPrincipal LocalUser user, @Valid @RequestBody EditUserBody body) {

		UserStatusDTO updated = userService.updateLoggedInUserProfile(user, body);

		return ResponseEntity.ok(updated);
	}

    @Operation(summary = "Logout user", description = "Logs out the currently authenticated user")
    @PostMapping("/logout")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<@NotNull LogoutResponseDTO> logoutUser(@AuthenticationPrincipal LocalUser user) {

        LogoutResponseDTO logoutResponse = userService.logoutUser(user);

        return ResponseEntity.ok(logoutResponse);
    }
}