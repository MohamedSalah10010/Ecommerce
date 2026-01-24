package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.Category;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepo extends JpaRepository<@NotNull Category, @NotNull Long>
{

	Optional<Category> findByIdAndIsDeleted(Long id, boolean isDeleted);
	List<Category> findAllByIsDeleted(boolean isDeleted);
}