package com.learn.ecommerce.services;

import com.learn.ecommerce.api.model.VerificationToken;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.api.model.LoginBody;
import com.learn.ecommerce.api.model.RegistrationBody;
import com.learn.ecommerce.exception.UserAlreadyExistsException;
import com.learn.ecommerce.model.LocalUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
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

    public LocalUser registerUser(@NotNull RegistrationBody body) throws UserAlreadyExistsException {
        if(userRepository.findByEmailIgnoreCase(body.getEmail()).isPresent()
            || userRepository.findByUserNameIgnoreCase(body.getUsername()).isPresent())
        {
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
        return userRepository.save(user);

    }

    private VerificationToken createVerificationToken(LocalUser user) {
        String token = jwtService.generateVerificationToken(user);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setExpiryDate( LocalDateTime.from(Instant.now().plusSeconds(60*60*24))); // 24 hours
        verificationToken.setUser(user);

        return verificationToken;
    }

    public String loginUser(LoginBody body) {
        Optional<LocalUser> opUser = userRepository.findByUserNameIgnoreCase(body.getUsername());
        if(opUser.isPresent())
        {
            LocalUser user = opUser.get();
            if (encryptionService.checkPassword(body.getPassword(), user.getPassword()))
            {
               return jwtService.generateToken(user);
            }
        }
            return null;
    }


}
