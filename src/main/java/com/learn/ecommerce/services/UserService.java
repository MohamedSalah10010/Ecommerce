package com.learn.ecommerce.services;

import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.api.model.LoginBody;
import com.learn.ecommerce.api.model.RegistrationBody;
import com.learn.ecommerce.exception.UserAlreadyExistsException;
import com.learn.ecommerce.model.LocalUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private LocalUserRepo userRepository;
    private EncryptionService encryptionService;
    private JwtService jwtService;

    public UserService(LocalUserRepo userRepository, EncryptionService encryptionService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }

    public LocalUser registerUser(RegistrationBody body) throws UserAlreadyExistsException {
        if(userRepository.findByEmailIgnoreCase(body.getEmail()).isPresent()
            || userRepository.findByUserNameIgnoreCase(body.getUsername()).isPresent())
        {
            throw new UserAlreadyExistsException();
        }

        LocalUser User = new LocalUser();
        User.setFirstName(body.getFirstName());
        User.setLastName(body.getLastName());
        User.setEmail(body.getEmail());

        User.setPassword(encryptionService.encryptPassword(body.getPassword()));
        User.setUserName(body.getUsername());

        return userRepository.save(User);

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
