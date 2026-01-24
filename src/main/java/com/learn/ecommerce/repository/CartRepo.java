package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.Cart;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.enums.CartStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepo extends JpaRepository<@NotNull Cart, @NotNull Long> {
    @Query("select c from Cart c where c.user = ?1")
    Optional<Cart> findByUser(LocalUser user);
    @Query("select c from Cart c where c.user.id = ?1")
    Optional<Cart> findByUserId(Long id);

    Optional<Cart> findByUserIdAndStatus(Long id, CartStatus cartStatus);
    Optional<Cart> findByIdAndUserId(Long id, Long cartId);

	Optional<Cart> findByIdAndIsDeleted(Long id, boolean isDeleted);
	List<Cart> findAllByIsDeleted(boolean isDeleted);
}