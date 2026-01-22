package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.Cart;
import com.learn.ecommerce.entity.LocalUser;
import com.learn.ecommerce.enums.CartStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CartRepo extends CrudRepository<Cart,Long> {
    @Query("select c from Cart c where c.user = ?1")
    Optional<Cart> findByUser(LocalUser user);
    @Query("select c from Cart c where c.user.id = ?1")
    Optional<Cart> findByUserId(Long id);

    Optional<Cart> findByUserIdAndStatus(Long id, CartStatus cartStatus);
    Optional<Cart> findByUserIdAndId(Long id, Long cartId);
}
