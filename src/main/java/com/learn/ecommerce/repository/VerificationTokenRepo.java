package com.learn.ecommerce.repository;

import com.learn.ecommerce.model.VerificationToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface VerificationTokenRepo extends CrudRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
