package com.learn.ecommerce.services;

import com.learn.ecommerce.DTO.UserRequestDTO.ForgetPasswordBodyDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.LoginBodyDTO;
import com.learn.ecommerce.DTO.UserRequestDTO.RegistrationBodyDTO;
import com.learn.ecommerce.entity.VerificationToken;
import com.learn.ecommerce.exceptionhandler.UserAlreadyExistsException;
import com.learn.ecommerce.exceptionhandler.UserIsNotVerifiedException;
import com.learn.ecommerce.exceptionhandler.UserNotFound;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.repository.LocalUserRepo;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private LocalUserRepo userRepository;
    private EncryptionService encryptionService;
    private JwtService jwtService;
    private EmailService emailService;

    public UserService(LocalUserRepo userRepository,
                       EncryptionService encryptionService,
                       JwtService jwtService, EmailService emailService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;

    }


    private VerificationToken createVerificationToken(LocalUser user) {
        String token = jwtService.generateVerificationToken(user);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24 hours
        verificationToken.setUser(user);

        return verificationToken;
    }

    @Transactional
    public LocalUser registerUser(@NotNull RegistrationBodyDTO body) throws UserAlreadyExistsException {
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
        return userRepository.save(user);

    }

    public String loginUser(LoginBodyDTO body) throws UserIsNotVerifiedException {
        Optional<LocalUser> opUser = userRepository.findByUserNameIgnoreCase(body.getUsername());
        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            if (encryptionService.checkPassword(body.getPassword(), user.getPassword())) {
                if (user.getIsVerified()) {
                    return jwtService.generateToken(user);
                } else {
                    List<VerificationToken> tokens = (List) user.getVerificationTokens();
                    boolean resend = tokens.size() == 0 ||
                            tokens.get(0).
                                    getCreatedAtTimeStamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
                    if (resend) {
                        VerificationToken verificationToken = createVerificationToken(user);
                        emailService.sendVerficationEmail(verificationToken);
                        user.getVerificationTokens().add(verificationToken);
                        userRepository.save(user);
                    }
                    throw new UserIsNotVerifiedException("User email is not verified.", resend);
                }

            }

        }

        return null;
    }

    @Transactional
    public boolean verifyUserEmail(String token) {
        Optional<LocalUser> opUser = userRepository.findByVerificationTokens_Token(token);
        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            Optional<VerificationToken> opToken = user.getVerificationTokens().stream()
                    .filter(t -> t.getToken().equals(token))
                    .findFirst();
            if (opToken.isPresent()) {
                VerificationToken verificationToken = opToken.get();
                if (verificationToken.getExpiryDate().isAfter(LocalDateTime.now())) {
                    if (!user.getIsVerified()) {
                        user.setIsVerified(true);
                    }


                    user.getVerificationTokens().remove(verificationToken);
                    userRepository.save(user);
                    return true;
                }
            }
        }
        return false;
    }


    @Transactional
    public void initiatePasswordReset(ForgetPasswordBodyDTO body) throws UserIsNotVerifiedException, UserNotFound {
        Optional<LocalUser> opUser = userRepository.findByUserNameIgnoreCase(body.getUsername());
        if (opUser.isPresent() && opUser.get().getEmail().equals(body.getEmail())) {

            LocalUser user = opUser.get();
            if (user.getIsVerified()) {
                String token = jwtService.generatePasswordResetToken(user);
                VerificationToken verificationToken = new VerificationToken();
                verificationToken.setToken(token);
                verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(30)); //30 minutes
                verificationToken.setUser(user);
                user.getVerificationTokens().add(verificationToken);
                userRepository.save(user);

                emailService.sendPasswordResetEmail(verificationToken);

            } else {
                throw new UserIsNotVerifiedException("User email is not verified.", false);
            }

        } else {
            throw new UserNotFound("User not found or email does not match.");
        }


    }

    @Transactional
    public boolean resetPassword(String newPassword, String token) throws UserNotFound {
//        String userEmail = jwtService.getPasswordResetEmail(token);
//        Optional<LocalUser> opUser = userRepository.findByEmailIgnoreCase(userEmail);
        Optional<LocalUser> opUser = userRepository.findByVerificationTokens_Token(token);


        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            List<VerificationToken> tokens = (List) user.getVerificationTokens();
            Optional<VerificationToken> opToken = tokens.stream()
                    .filter(t -> t.getToken().equals(token))
                    .findFirst();
            if (opToken.isPresent()) {
                VerificationToken verificationToken = opToken.get();
                if (verificationToken.getExpiryDate().isAfter(LocalDateTime.now())) {
                    user.setPassword(encryptionService.encryptPassword(newPassword));
                    user.getVerificationTokens().remove(verificationToken);
                    userRepository.save(user);
                    return true;
                }
            }
        } else {
            throw new UserNotFound("Invalid password reset token.");
        }
        return false;
    }

}
