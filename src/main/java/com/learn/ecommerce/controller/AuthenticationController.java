package com.learn.ecommerce.controller;

import com.learn.ecommerce.api.model.RegistrationBody;
import com.learn.ecommerce.exception.UserAlreadyExistsException;
import com.learn.ecommerce.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private UserServices userServices;

    public AuthenticationController(UserServices userServices) {
        this.userServices = userServices;
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody) {

        try {
            userServices.registerUser(registrationBody);
//            System.out.println(registrationBody.toString());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }


    }
}
