package com.learn.ecommerce.services;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.learn.ecommerce.DTO.Address.AddressDTO;
import com.learn.ecommerce.DTO.Roles.RolesDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.*;
import com.learn.ecommerce.DTO.UserResponseDTO.LoginResponseDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.LogoutResponseDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserDTO;
import com.learn.ecommerce.DTO.UserResponseDTO.UserStatusDTO;
import com.learn.ecommerce.entity.Address;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.entity.LoginTokens;
import com.learn.ecommerce.entity.VerificationToken;
import com.learn.ecommerce.exceptionhandler.*;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.repository.LoginTokensRepo;
import com.learn.ecommerce.repository.VerificationTokenRepo;
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
    private  final LocalUserRepo userRepository;
    private final EncryptionService encryptionService;
    private final JwtService jwtService;
    private final EmailService emailService;
	private final VerificationTokenRepo verificationTokenRepo;

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
            throw new UserAlreadyExistsException("user already exists");
        }

        LocalUser user = new LocalUser();
        user.setFirstName(body.getFirstName());
        user.setLastName(body.getLastName());
        user.setEmail(body.getEmail());
		user.setPhoneNumber(body.getPhoneNumber());
        user.setPassword(encryptionService.encryptPassword(body.getPassword()));
        user.setUserName(body.getUsername());

	    Address address = Address.builder()
						.user(user)
						.addressLine1(body.getAddress().getAddressLine1())
						.addressLine2(body.getAddress().getAddressLine2())
						.city(body.getAddress().getCity())
						.country(body.getAddress().getCountry()).build();
		user.getAddresses().add(address);

        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        user.getVerificationTokens().add(verificationToken);
        userRepository.save(user);

        log.info("User registered successfully: username={}", user.getUsername());
	    List<AddressDTO> addressDTO = user.getAddresses().stream().filter(address1-> !address1.isDeleted()).map(address1->
					    new AddressDTO(
							    address1.getAddressLine1(),
							    address1.getAddressLine1(),
							    address1.getCity(),
							    address1.getCountry()))
			    .toList();
		List<RolesDTO> rolesDTOS = user.getUserRoles().stream().filter(roles-> !roles.isDeleted()).map(roles->
			 new RolesDTO(roles.getRoleName())).toList();

	    return UserDTO.builder()
			    .id(user.getId())
			    .userName(user.getUsername())
			    .email(user.getEmail())
			    .firstName(user.getFirstName())
			    .lastName(user.getLastName())
			    .phoneNumber(user.getPhoneNumber())
			    .roles(rolesDTOS)
			    .addresses( addressDTO)
			    .isVerified(user.isVerified())
			    .isEnabled(user.isEnabled())
			    .createdAt(user.getCreatedAt())
			    .updatedAt(user.getUpdatedAt())
			    .build();	}


    public UserDTO getUserProfile(LocalUser user) {
        log.info("Fetching profile for userId={}", user.getId());

        LocalUser localUser = userRepository.findByIdAndIsDeleted(user.getId(),false)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", user.getId());
                    return new UserNotFoundException("user not found");
                });

        log.info("Profile fetched successfully for user {}", localUser.getUsername());
	    List<AddressDTO> addressDTO = localUser.getAddresses().stream().filter(address-> !address.isDeleted()).map(address->
			    new AddressDTO(
						address.getAddressLine1(),
					    address.getAddressLine1(),
					    address.getCity(),
					    address.getCountry()))
			    .toList();
	    List<RolesDTO> rolesDTOS = user.getUserRoles().stream().filter(roles-> !roles.isDeleted()).map(roles->
			    new RolesDTO(roles.getRoleName())).toList();

		     return UserDTO.builder()
                .id(localUser.getId())
                .userName(localUser.getUsername())
                .email(localUser.getEmail())
                .firstName(localUser.getFirstName())
                .lastName(localUser.getLastName())
                .phoneNumber(localUser.getPhoneNumber())
                .roles(rolesDTOS)
                .addresses( addressDTO)
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
                    return new UserNotFoundException("user not found");
                });

		if(user.isDeleted()) {
			log.warn("Login failed: user is deleted {}", body.getUsername());
			throw new UserNotFoundException("user not found");
		}
        if (!encryptionService.checkPassword(body.getPassword(), user.getPassword())) {
            log.warn("Login failed: invalid credentials for user {}", body.getUsername());
            throw new InvalidCredentialsException("invalid credentials for user");
        }

        if (!user.isVerified()) {
            log.warn("Login failed: user not verified {}", body.getUsername());
            throw new UserIsNotVerifiedException("user is not verified");
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


	    VerificationToken verificationToken =  verificationTokenRepo.findByTokenAndIsDeleted(token,false)
			    .orElseThrow(() -> new TokenNotFoundException("Token not found"));


	    if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
		    throw new TokenExpiredException("Token has expired", Instant.now());
	    }
	    LocalUser user = verificationToken.getUser();

        if (!user.isVerified()) {
            user.setVerified(true);
            user.setEnabled(true);
            log.info("User email verified: {}", user.getUsername());
        }
		verificationToken.setDeleted(true);
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
                    return new UserNotFoundException("user not found");
                });

	    if(user.isDeleted()) {
		    log.warn("User is deleted for email verification: {}", body.getUsername());
		    throw new UserNotFoundException("user not found");
	    }

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
                    return new UserNotFoundException("user not found ");
                });

        if (!user.getEmail().equals(body.getEmail())) {
            log.warn("Password reset failed: email mismatch for user {}", body.getUsername());
            throw new UserNotFoundException("email mismatch");
        }

        if (!user.isVerified()) {
            log.warn("Password reset failed: user not verified {}", body.getUsername());
            throw new UserIsNotVerifiedException("user is not verified");
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


        VerificationToken verificationToken =  verificationTokenRepo.findByTokenAndIsDeleted(token,false)
					    .orElseThrow(() -> new TokenNotFoundException("Token not found"));

	    if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
		    throw new TokenExpiredException("Token has expired", Instant.now());
	    }

	    LocalUser user = verificationToken.getUser();
	    log.info("Resetting password for user={}", user.getUsername());
	    user.setPassword(encryptionService.encryptPassword(newPassword));
	    verificationToken.setDeleted(true);
		logoutUser(user);
        userRepository.save(user);

        log.info("Password reset successfully for user {}", user.getUsername());
        return UserStatusDTO.builder()
                .passwordChangedSuccess(true)
                .statusMessage("Password changed successfully.")
		        .isVerified(null)
		        .isVerified(null)
                .build();
    }

    @Transactional
    public UserStatusDTO updateLoggedInUserProfile(LocalUser user, EditUserBody body) {
        log.info("Updating profile for user={}", user.getUsername());


        if (body.getEmail() != null) {
			user.setEmail(body.getEmail());
			user.setVerified(false);
			log.info("New Email  required verification for user {}", user.getUsername());
			RequestEmailVerificationDTO requestEmailVerificationDTO = new RequestEmailVerificationDTO();
			requestEmailVerificationDTO.setEmail(body.getEmail());
			requestEmailVerificationDTO.setUsername(user.getUsername());
			requestEmailVerification(requestEmailVerificationDTO);
			log.info("New Email verification request sent for user {}", user.getUsername());
		}
        if (body.getUsername() != null) user.setUserName(body.getUsername());
        if (body.getFirstName() != null) user.setFirstName(body.getFirstName());
        if (body.getLastName() != null) user.setLastName(body.getLastName());
        if (body.getPhoneNumber() != null) user.setPhoneNumber(body.getPhoneNumber());
		if(body.getAddress() != null)
		{
			Address address = new Address();
			if (body.getAddress().getAddressLine1()!= null) address.setAddressLine1(body.getAddress().getAddressLine1());
			if (body.getAddress().getAddressLine2()!= null) address.setAddressLine2(body.getAddress().getAddressLine2());
			if (body.getAddress().getCity()!= null) address.setCity(body.getAddress().getCity());
			if (body.getAddress().getCountry()!= null) address.setCountry(body.getAddress().getCountry());

			user.getAddresses().add(address);
		}

        userRepository.save(user);
        log.info("User profile updated successfully for user={}", user.getUsername());

        return UserStatusDTO.builder()
                .statusMessage("Profile updated successfully.")
		        .isVerified(user.isVerified())
		        .isEnabled(user.isEnabled())
		        .updatedBy(user.getUpdatedBy())
		        .updatedAt(Instant.now())
                .build();
    }

	@Transactional
	public UserStatusDTO updateUserProfileById(Long userId, EditUserBody body) {
		log.info("Updating profile for userId={}", userId);

		LocalUser user = userRepository.findByIdAndIsDeleted(userId,false)
				.orElseThrow(() -> {
					log.warn("User not found for profile update: id={}", userId);
					return new UserNotFoundException("user not found");
				});

		if (body.getEmail() != null) user.setEmail(body.getEmail());
		if (body.getUsername() != null) user.setUserName(body.getUsername());
		if (body.getFirstName() != null) user.setFirstName(body.getFirstName());
		if (body.getLastName() != null) user.setLastName(body.getLastName());
		if (body.getPhoneNumber() != null) user.setPhoneNumber(body.getPhoneNumber());
		if(body.getAddress() != null)
		{
			Address address = new Address();
			if (body.getAddress().getAddressLine1()!= null) address.setAddressLine1(body.getAddress().getAddressLine1());
			if (body.getAddress().getAddressLine2()!= null) address.setAddressLine2(body.getAddress().getAddressLine2());
			if (body.getAddress().getCity()!= null) address.setCity(body.getAddress().getCity());
			if (body.getAddress().getCountry()!= null) address.setCountry(body.getAddress().getCountry());

			user.getAddresses().add(address);
		}

		userRepository.save(user);
		log.info("User profile updated successfully for user={}", user.getUsername());

		return UserStatusDTO.builder()
				.statusMessage("Profile updated successfully.")
				.isVerified(user.isVerified())
				.isEnabled(user.isEnabled())
				.updatedBy(user.getUpdatedBy())
				.updatedAt(Instant.now())
				.build();
	}

    @Transactional
    public LogoutResponseDTO logoutUser(LocalUser user) {
        log.info("Logging out user {}", user.getUsername());

        List<LoginTokens> tokens = loginTokensRepo.findAllByUserAndIsDeleted(user,false);
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