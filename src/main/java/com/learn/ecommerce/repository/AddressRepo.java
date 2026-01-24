package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.Address;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepo  extends JpaRepository<@NotNull Address, @NotNull Long> {

	Optional<Address> findByIdAndIsDeleted(Long id, boolean isDeleted);
	List<Address> findAllByIsDeleted(boolean isDeleted);
}