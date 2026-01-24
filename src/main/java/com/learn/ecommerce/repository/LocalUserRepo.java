package com.learn.ecommerce.repository;


import com.learn.ecommerce.entity.LocalUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocalUserRepo extends JpaRepository<@NotNull LocalUser, @NotNull Long> {
    Optional<LocalUser> findByUserNameIgnoreCase(String userName);

    Optional<LocalUser> findByEmailIgnoreCase(String email);

    Optional<LocalUser> findByVerificationTokens_Token(String token);

	Optional<LocalUser> findByIdAndIsDeleted(Long id, boolean isDeleted);
	List<LocalUser> findAllByIsDeleted(boolean isDeleted);

	Optional<LocalUser> findByIdAndIsLocked(Long id, boolean isLocked);

	Optional<LocalUser> findByIdAndIsEnabled(Long id, boolean isEnabled);

	Optional<LocalUser> findByIdAndIsVerified(Long id, boolean isVerified);

	List<LocalUser> findAllByIsDeletedAndIsLockedAndIsVerifiedAndIsEnabled( boolean isDeleted, boolean isLocked, boolean isVerified, boolean isEnabled);

}