package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.entity.LoginTokens;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LoginTokensRepo extends JpaRepository<@NotNull LoginTokens, @NotNull String> {

    Optional<Collection<LoginTokens>> findLoginTokensByUser(LocalUser user);

    List<LoginTokens> findAllByUserAndIsDeleted(LocalUser user, boolean isDeleted);
    Optional<LoginTokens> findByToken(String token);

	List<LoginTokens> findAllByIsDeleted(boolean isDeleted);
	Optional<LoginTokens> findByIdAndIsDeleted(Long id, boolean isDeleted);
}