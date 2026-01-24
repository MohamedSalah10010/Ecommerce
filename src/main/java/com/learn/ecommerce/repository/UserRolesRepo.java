package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.UserRoles;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRolesRepo extends JpaRepository<@NotNull UserRoles, @NotNull Long> {

    Optional<UserRoles> findByRoleName(String roleName);
	Optional<UserRoles> findByIdAndIsDeleted(Long id, boolean isDeleted);
	List<UserRoles> findAllByIsDeleted(boolean isDeleted);
}