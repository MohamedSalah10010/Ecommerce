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
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

        return verificationToken;
    }

    @Transactional
    public UserDTO registerUser(@NotNull RegistrationBodyDTO body) {
        if (userRepository.findByEmailIgnoreCase(body.getEmail()).isPresent()
                || userRepository.findByUserNameIgnoreCase(body.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }


        LocalUser user = new LocalUser();
        user.setFirstName(body.getFirstName());
        user.setLastName(body.getLastName());
        user.setEmail(body.getEmail());

        user.setPassword(encryptionService.encryptPassword(body.getPassword()));
        user.setUserName(body.getUsername());
        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerficationEmail(verificationToken);
        user.getVerificationTokens().add(verificationToken);
        userRepository.save(user);


        return ObjectMapperUtils.map(user, UserDTO.class);

    }


    public UserDTO getUserProfile(LocalUser user) {
        Optional<LocalUser> opUser = userRepository.findById(user.getId());
        if (opUser.isPresent()) {
            LocalUser localUser = opUser.get();
//            return ObjectMapperUtils.map(localUser, UserDTO.class);
        return new UserDTO()
                .builder()
                .id(localUser.getId())
                .userName(localUser.getUsername())
                .email(localUser.getEmail())
                .firstName(localUser.getFirstName())
                .lastName(localUser.getLastName())
                .phoneNumber(localUser.getPhoneNumber())
                .roles(localUser.getUserRoles())
                .addresses(localUser.getAddresses())
                .isVerified(localUser.getIsVerified())
                .isEnabled(localUser.getIsEnabled())
                .createdAt(localUser.getCreatedAt())
                .updatedAt(localUser.getUpdatedAt())
                .build();
        }

        throw new UserNotFoundException();

    }

    public LoginResponseDTO loginUser(LoginBodyDTO body)  {
        Optional<LocalUser> opUser = userRepository.findByUserNameIgnoreCase(body.getUsername());
        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            if (encryptionService.checkPassword(body.getPassword(), user.getPassword())) {
                if (user.getIsVerified()) {
                    LoginTokens loginToken = new LoginTokens();
                    loginToken.setUser(user);
                    loginToken.setToken(jwtService.generateToken(user));
                    loginToken.setIssuedAt(Instant.now());
                    loginToken.setExpiresAt(Instant.now().plusSeconds(jwtService.getExpiryInSeconds()));
                    loginToken.setExpired(false);
                    loginToken.setRevoked(false);
                    loginTokensRepo.save(loginToken);

                    return  new LoginResponseDTO().builder()
                            .jwt(loginToken.getToken())
                            .success(true)
                            .failureMessage(null)
                            .build();
                }

                throw new UserIsNotVerifiedException();
            }

            throw new InvalidCredentialsException();
        }

        throw new UserNotFoundException();

    }

    @Transactional
    public UserStatusDTO verifyUserEmail(String token) {
        Optional<LocalUser> opUser = userRepository.findByVerificationTokens_Token(token);
        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            Optional<VerificationToken> opToken = user.getVerificationTokens().stream()
                    .filter(t -> t.getToken().equals(token))
                    .findFirst();
            if (opToken.isPresent())
            {
                VerificationToken verificationToken = opToken.get();
                if (verificationToken.getExpiryDate().isAfter(Instant.now())) {
                    if (!user.getIsVerified()) {
                        user.setIsVerified(true);
                        user.setIsEnabled(true);
                    }

                    user.getVerificationTokens().remove(verificationToken);
                    userRepository.save(user);
                    return new UserStatusDTO()
                            .builder()
                            .email(user.getEmail())
                            .isVerified(true)
                            .isEnabled(true)
                            .build();
                }
                throw new TokenExpiredException("Token has expired", Instant.now());
            }
            throw new TokenNotFoundException();
        }
        throw new UserNotFoundException();
    }

    @Transactional
    public void requestEmailVerification( RequestEmailVerificationDTO body){
        Optional<LocalUser> optionalUser= userRepository.findByUserNameIgnoreCase(body.getUsername());
        if (optionalUser.isPresent())
        {
            LocalUser user = optionalUser.get();
           if(user.getEmail().equals(body.getEmail()))
               {

                   VerificationToken verificationToken = createVerificationToken(optionalUser.get());
                   emailService.sendVerficationEmail(verificationToken);
                   user.getVerificationTokens().add(verificationToken);
                   userRepository.save(user);
                    return;
               }
           throw new EmailFailureException("Email not correct");
        }
        throw new UserNotFoundException();
    }


    @Transactional
    public void initiatePasswordReset(ForgetPasswordBodyDTO body) {
        Optional<LocalUser> opUser = userRepository.findByUserNameIgnoreCase(body.getUsername());
        if (opUser.isPresent() && opUser.get().getEmail().equals(body.getEmail())) {

            LocalUser user = opUser.get();
            if (user.getIsVerified())
            {
                String token = jwtService.generatePasswordResetToken(user);
                VerificationToken verificationToken = new VerificationToken();
                verificationToken.setToken(token);
                verificationToken.setExpiryDate(Instant.now().plusSeconds(60*60*30)); //30 minutes
                verificationToken.setUser(user);
                user.getVerificationTokens().add(verificationToken);
                userRepository.save(user);

                emailService.sendPasswordResetEmail(verificationToken);
                return;
            }
            throw new UserIsNotVerifiedException();

        }

        throw new UserNotFoundException();



    }

    @Transactional
    public UserStatusDTO resetPassword(String newPassword, String token)  {
//        String userEmail = jwtService.getPasswordResetEmail(token);
//        Optional<LocalUser> opUser = userRepository.findByEmailIgnoreCase(userEmail);
        Optional<LocalUser> opUser = userRepository.findByVerificationTokens_Token(token);


        if (opUser.isPresent())
        {
            LocalUser user = opUser.get();
            List<VerificationToken> tokens = (List) user.getVerificationTokens();
            Optional<VerificationToken> opToken = tokens.stream()
                    .filter(t -> t.getToken().equals(token))
                    .findFirst();
            if (opToken.isPresent()) {
                VerificationToken verificationToken = opToken.get();
                if (verificationToken.getExpiryDate().isAfter(Instant.now())) {
                    user.setPassword(encryptionService.encryptPassword(newPassword));
                    user.getVerificationTokens().remove(verificationToken);
                    userRepository.save(user);
                    return new  UserStatusDTO()
                            .builder()
                            .passwordChangedSuccess(true)
                            .statusMessage("Password changed successfully.")
                            .build();
                }

                throw new TokenExpiredException("Token has expired", Instant.now());


            }
            throw new TokenNotFoundException();
        }

        throw new UserNotFoundException();


    }

    @Transactional
    public UserStatusDTO updateUserProfile(Long userId, EditUserBody body) {
        Optional<LocalUser> opUser = userRepository.findById(userId);
        if (opUser.isPresent()) {
            LocalUser localUser = opUser.get();

            if(body.getEmail()!= null)
                localUser.setEmail(body.getEmail());
            if(body.getUsername()!= null)
                localUser.setUserName(body.getUsername());
           if (body.getFirstName()!= null)
                localUser.setFirstName(body.getFirstName());
           if(body.getLastName()!= null)
                localUser.setLastName(body.getLastName());
           if(body.getPhoneNumber()!= null)
                localUser.setPhoneNumber(body.getPhoneNumber());

            userRepository.save(localUser);
            return new UserStatusDTO()
                    .builder()
                    .statusMessage("Profile updated successfully.")
                    .build();
        }

        throw new UserNotFoundException();
    }


    @Transactional
    public LogoutResponseDTO logoutUser(LocalUser user) {

        List<LoginTokens> tokens = loginTokensRepo.findAllByUser(user);
           if(!tokens.isEmpty())
           {
               for (LoginTokens token : tokens)
               {
                   token.setExpired(true);
                   token.setRevoked(true);
               }
               // saveAll NOT required because of @Transactional
               //loginTokensRepo.saveAll(tokens);
               return new LogoutResponseDTO()
                       .builder()
                       .logoutMessage("User logged out successfully.")
                       .build();
           }

           return new LogoutResponseDTO().builder()
                     .logoutMessage("No active sessions found for the user.")
                     .build();

    }
}
