package com.learn.ecommerce.repository;


import com.learn.ecommerce.model.LocalUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LocalUserRepo extends CrudRepository<LocalUser,Long> {
    Optional<LocalUser> findByUserNameIgnoreCase(String userName);

    Optional<LocalUser> findByEmailIgnoreCase(String email);

    Optional<LocalUser> findByVerificationTokens_Token(String token);

}
