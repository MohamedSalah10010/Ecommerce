package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.entity.LoginTokens;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LoginTokensRepo extends CrudRepository<LoginTokens, String> {

    Optional<Collection<LoginTokens>> findLoginTokensByUser(LocalUser user);

    List<LoginTokens> findAllByUser(LocalUser user);
    Optional<LoginTokens> findByToken(String token);
}
