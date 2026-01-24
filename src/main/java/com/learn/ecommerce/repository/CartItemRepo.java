package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.Cart;
import com.learn.ecommerce.entity.CartItem;
import com.learn.ecommerce.entity.Product;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepo extends JpaRepository<@NotNull CartItem, @NotNull Long> {

	Optional<CartItem> findByIdAndIsDeleted(Long id, boolean isDeleted);
	List<CartItem> findAllByIsDeleted(boolean isDeleted);

Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
Optional<CartItem> findByIdAndCartId(Long itemId,Long cartId);

}