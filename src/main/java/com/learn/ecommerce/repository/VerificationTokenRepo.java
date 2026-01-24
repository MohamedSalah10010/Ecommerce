package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.VerificationToken;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepo extends JpaRepository<@NotNull VerificationToken, @NotNull Long> {
   Optional< VerificationToken> findByToken(String token);
	Optional<VerificationToken> findByTokenAndIsDeleted(String token, boolean isDeleted);
	List<VerificationToken> findAllByIsDeleted(boolean isDeleted);
}