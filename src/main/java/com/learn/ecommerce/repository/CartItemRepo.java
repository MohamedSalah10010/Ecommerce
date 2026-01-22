package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.Cart;
import com.learn.ecommerce.entity.CartItem;
import com.learn.ecommerce.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CartItemRepo extends CrudRepository<CartItem,Long> {

Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
Optional<CartItem> findByIdAndCartId(Long itemId,Long cartId);

}
