package com.learn.ecommerce.repository;

import com.learn.ecommerce.api.model.VerificationToken;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepo extends CrudRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
