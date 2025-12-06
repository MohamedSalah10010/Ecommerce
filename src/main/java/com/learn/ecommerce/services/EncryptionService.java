package com.learn.ecommerce.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EncryptionService {
    @Value("${encryption.salt.round}")
    private int saltRound;

    private String salt;

    @PostConstruct
    public void postConstruct(){
        this.salt = BCrypt.gensalt(saltRound);
    }

    public String encryptPassword(String password){
        return BCrypt.hashpw(password, salt);
    }

    public Boolean checkPassword(String password, String hashedPassword){
        return BCrypt.checkpw(password, hashedPassword);
    }
}
