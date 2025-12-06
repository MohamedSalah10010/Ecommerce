package com.learn.ecommerce.services;

import com.learn.ecommerce.Repositery.LocalUserRepo;
import com.learn.ecommerce.api.model.RegistrationBody;
import com.learn.ecommerce.exception.UserAlreadyExistsException;
import com.learn.ecommerce.model.LocalUser;
import org.springframework.stereotype.Service;

@Service
public class UserServices {

    private LocalUserRepo userRepository;

    public UserServices(LocalUserRepo userRepository) {
        this.userRepository = userRepository;
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
        //TODO: encrypt password
        User.setPassword(body.getPassword());
        User.setUserName(body.getUsername());

        return userRepository.save(User);

    }
}
