package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.UserRoles;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRolesRepo extends CrudRepository<UserRoles,Long> {

    Optional<UserRoles> findByRoleName(String roleName);
}
