package com.learn.ecommerce.services;

import com.learn.ecommerce.api.model.LoginBody;
import com.learn.ecommerce.api.model.RegistrationBody;
import com.learn.ecommerce.api.model.VerificationToken;
import com.learn.ecommerce.exception.UserAlreadyExistsException;
import com.learn.ecommerce.exception.UserIsNotVerifiedException;
import com.learn.ecommerce.model.LocalUser;
import com.learn.ecommerce.repository.LocalUserRepo;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

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
        verificationToken.setExpiryDate( LocalDateTime.now().plusHours(24)); // 24 hours
        verificationToken.setUser(user);

        return verificationToken;
    }

    public LocalUser registerUser(@NotNull RegistrationBody body) throws UserAlreadyExistsException
    {
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
        user.getVerificationTokens().add(verificationToken);
        return userRepository.save(user);

    }

    public String loginUser(LoginBody body) throws UserIsNotVerifiedException {
        Optional<LocalUser> opUser = userRepository.findByUserNameIgnoreCase(body.getUsername());
        if(opUser.isPresent() )
        {
            LocalUser user = opUser.get();
            if (encryptionService.checkPassword(body.getPassword(), user.getPassword()))
            {
                if(user.getIsVerified())
                {
                    return jwtService.generateToken(user);
                }
                else
                {
                    List<VerificationToken> tokens = (List) user.getVerificationTokens();
                    boolean resend = tokens.size() == 0 ||
                            tokens.get(0).getCreatedAtTimeStamp().before(new Timestamp(System.currentTimeMillis()-(60*60*1000)));
                    if(resend)
                    {
                        VerificationToken verificationToken = createVerificationToken(user);
                        emailService.sendVerficationEmail(verificationToken);
                        user.getVerificationTokens().add(verificationToken);
                        userRepository.save(user);
                    }
                    throw new UserIsNotVerifiedException("User email is not verified.", resend );
                }

            }

        }

        return null;
    }

    public boolean verifyUserEmail(String token) {
        Optional<LocalUser> opUser = userRepository.findByVerificationTokens_Token(token);
        if(opUser.isPresent())
        {
            LocalUser user = opUser.get();
            Optional<VerificationToken> opToken = user.getVerificationTokens().stream()
                    .filter(t -> t.getToken().equals(token))
                    .findFirst();
            if(opToken.isPresent())
            {
                VerificationToken verificationToken = opToken.get();
                if(verificationToken.getExpiryDate().isAfter(LocalDateTime.now()))
                {
                    user.setIsVerified(true);
                    userRepository.save(user);
                    return true;
                }
            }
        }
        return false;
    }

}
