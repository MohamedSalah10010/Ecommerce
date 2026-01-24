package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.Inventory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface   InventoryRepo extends JpaRepository<@NotNull Inventory, @NotNull Long> {
	Optional<Inventory> findByIdAndIsDeleted(Long id, boolean isDeleted);
	List<Inventory> findAllByIsDeleted(boolean isDeleted);
}