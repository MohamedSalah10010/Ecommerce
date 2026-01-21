package com.learn.ecommerce.services;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.learn.ecommerce.DTO.UserRequestDTO.*;
import com.learn.ecommerce.DTO.UserResponseDTO.LoginResponseDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.LogoutResponseDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserStatusDTO;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.entity.LoginTokens;
import com.learn.ecommerce.entity.VerificationToken;
import com.learn.ecommerce.exceptionhandler.*;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.repository.LoginTokensRepo;
import com.learn.ecommerce.utils.ObjectMapperUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@Service
public class UserService {

    private final LoginTokensRepo loginTokensRepo;
    private LocalUserRepo userRepository;
    private EncryptionService encryptionService;
    private JwtService jwtService;
    private EmailService emailService;

    private VerificationToken createVerificationToken(LocalUser user) {
        String token = jwtService.generateVerificationToken(user);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(Instant.now().plusSeconds(60*60*12)); // 12 hours
        verificationToken.setUser(user);
        log.info("Created verification token for user {}", user.getUsername());
        return verificationToken;
    }

    @Transactional
    public UserDTO registerUser(@NotNull RegistrationBodyDTO body) {
        log.info("Registering user: username={}, email={}", body.getUsername(), body.getEmail());

        if (userRepository.findByEmailIgnoreCase(body.getEmail()).isPresent()
                || userRepository.findByUserNameIgnoreCase(body.getUsername()).isPresent()) {
            log.warn("Registration failed: user already exists with username/email {}", body.getUsername());
            throw new UserAlreadyExistsException();
        }

        LocalUser user = new LocalUser();
        user.setFirstName(body.getFirstName());
        user.setLastName(body.getLastName());
        user.setEmail(body.getEmail());
        user.setPassword(encryptionService.encryptPassword(body.getPassword()));
        user.setUserName(body.getUsername());

        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        user.getVerificationTokens().add(verificationToken);
        userRepository.save(user);

        log.info("User registered successfully: username={}", user.getUsername());
        return ObjectMapperUtils.map(user, UserDTO.class);
    }

    public UserDTO getUserProfile(LocalUser user) {
        log.info("Fetching profile for userId={}", user.getId());

        LocalUser localUser = userRepository.findById(user.getId())
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", user.getId());
                    return new UserNotFoundException();
                });

        log.info("Profile fetched successfully for user {}", localUser.getUsername());
        return UserDTO.builder()
                .id(localUser.getId())
                .userName(localUser.getUsername())
                .email(localUser.getEmail())
                .firstName(localUser.getFirstName())
                .lastName(localUser.getLastName())
                .phoneNumber(localUser.getPhoneNumber())
                .roles(localUser.getUserRoles())
                .addresses(localUser.getAddresses())
                .isVerified(localUser.isVerified())
                .isEnabled(localUser.isEnabled())
                .createdAt(localUser.getCreatedAt())
                .updatedAt(localUser.getUpdatedAt())
                .build();
    }

    public LoginResponseDTO loginUser(LoginBodyDTO body) {
        log.info("Login attempt for username={}", body.getUsername());

        LocalUser user = userRepository.findByUserNameIgnoreCase(body.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found {}", body.getUsername());
                    return new UserNotFoundException();
                });

        if (!encryptionService.checkPassword(body.getPassword(), user.getPassword())) {
            log.warn("Login failed: invalid credentials for user {}", body.getUsername());
            throw new InvalidCredentialsException();
        }

        if (!user.isVerified()) {
            log.warn("Login failed: user not verified {}", body.getUsername());
            throw new UserIsNotVerifiedException();
        }

        LoginTokens loginToken = new LoginTokens();
        loginToken.setUser(user);
        loginToken.setToken(jwtService.generateToken(user));
        loginToken.setIssuedAt(Instant.now());
        loginToken.setExpiresAt(Instant.now().plusSeconds(jwtService.getExpiryInSeconds()));
        loginToken.setExpired(false);
        loginToken.setRevoked(false);
        loginTokensRepo.save(loginToken);

        log.info("User logged in successfully: username={}", user.getUsername());
        return LoginResponseDTO.builder()
                .jwt(loginToken.getToken())
                .success(true)
                .failureMessage(null)
                .build();
    }

    @Transactional
    public UserStatusDTO verifyUserEmail(String token) {
        log.info("Verifying email with token={}", token);

        LocalUser user = userRepository.findByVerificationTokens_Token(token)
                .orElseThrow(() -> {
                    log.warn("Verification failed: user not found for token={}", token);
                    return new UserNotFoundException();
                });

        VerificationToken verificationToken = user.getVerificationTokens().stream()
                .filter(t -> t.getToken().equals(token))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Verification failed: token not found={}", token);
                    return new TokenNotFoundException();
                });

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Verification token expired for user {}", user.getUsername());
            throw new TokenExpiredException("Token has expired", Instant.now());
        }

        if (!user.isVerified()) {
            user.setVerified(true);
            user.setEnabled(true);
            log.info("User email verified: {}", user.getUsername());
        }

        user.getVerificationTokens().remove(verificationToken);
        userRepository.save(user);

        return UserStatusDTO.builder()
                .email(user.getEmail())
                .isVerified(true)
                .isEnabled(true)
                .build();
    }

    @Transactional
    public void requestEmailVerification(RequestEmailVerificationDTO body) {
        log.info("Requesting email verification for username={}", body.getUsername());

        LocalUser user = userRepository.findByUserNameIgnoreCase(body.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found for email verification: {}", body.getUsername());
                    return new UserNotFoundException();
                });

        if (!user.getEmail().equals(body.getEmail())) {
            log.warn("Email verification failed: email mismatch for user {}", body.getUsername());
            throw new EmailFailureException("Email not correct");
        }

        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        user.getVerificationTokens().add(verificationToken);
        userRepository.save(user);

        log.info("Verification email sent successfully to user {}", body.getUsername());
    }

    @Transactional
    public void initiatePasswordReset(ForgetPasswordBodyDTO body) {
        log.info("Initiating password reset for username={}", body.getUsername());

        LocalUser user = userRepository.findByUserNameIgnoreCase(body.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found for password reset: {}", body.getUsername());
                    return new UserNotFoundException();
                });

        if (!user.getEmail().equals(body.getEmail())) {
            log.warn("Password reset failed: email mismatch for user {}", body.getUsername());
            throw new UserNotFoundException();
        }

        if (!user.isVerified()) {
            log.warn("Password reset failed: user not verified {}", body.getUsername());
            throw new UserIsNotVerifiedException();
        }

        String token = jwtService.generatePasswordResetToken(user);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(Instant.now().plusSeconds(60*30)); // 30 minutes
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        userRepository.save(user);

        emailService.sendPasswordResetEmail(verificationToken);
        log.info("Password reset token generated and sent for user {}", body.getUsername());
    }

    @Transactional
    public UserStatusDTO resetPassword(String newPassword, String token) {
        log.info("Resetting password using token={}", token);

        LocalUser user = userRepository.findByVerificationTokens_Token(token)
                .orElseThrow(() -> {
                    log.warn("User not found for password reset with token={}", token);
                    return new UserNotFoundException();
                });

        VerificationToken verificationToken = user.getVerificationTokens().stream()
                .filter(t -> t.getToken().equals(token))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Password reset failed: token not found={}", token);
                    return new TokenNotFoundException();
                });

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Password reset token expired for user {}", user.getUsername());
            throw new TokenExpiredException("Token has expired", Instant.now());
        }

        user.setPassword(encryptionService.encryptPassword(newPassword));
        user.getVerificationTokens().remove(verificationToken);
        userRepository.save(user);

        log.info("Password reset successfully for user {}", user.getUsername());
        return UserStatusDTO.builder()
                .passwordChangedSuccess(true)
                .statusMessage("Password changed successfully.")
                .build();
    }

    @Transactional
    public UserStatusDTO updateUserProfile(Long userId, EditUserBody body) {
        log.info("Updating profile for userId={}", userId);

        LocalUser user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for profile update: id={}", userId);
                    return new UserNotFoundException();
                });

        if (body.getEmail() != null) user.setEmail(body.getEmail());
        if (body.getUsername() != null) user.setUserName(body.getUsername());
        if (body.getFirstName() != null) user.setFirstName(body.getFirstName());
        if (body.getLastName() != null) user.setLastName(body.getLastName());
        if (body.getPhoneNumber() != null) user.setPhoneNumber(body.getPhoneNumber());

        userRepository.save(user);
        log.info("User profile updated successfully for userId={}", userId);

        return UserStatusDTO.builder()
                .statusMessage("Profile updated successfully.")
                .build();
    }

    @Transactional
    public LogoutResponseDTO logoutUser(LocalUser user) {
        log.info("Logging out user {}", user.getUsername());

        List<LoginTokens> tokens = loginTokensRepo.findAllByUser(user);
        if (!tokens.isEmpty()) {
            tokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            log.info("User {} logged out, {} tokens expired/revoked", user.getUsername(), tokens.size());
            return LogoutResponseDTO.builder()
                    .logoutMessage("User logged out successfully.")
                    .build();
        }

        log.info("No active sessions found for user {}", user.getUsername());
        return LogoutResponseDTO.builder()
                .logoutMessage("No active sessions found for the user.")
                .build();
    }
}
