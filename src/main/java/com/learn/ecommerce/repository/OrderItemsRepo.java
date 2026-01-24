package com.learn.ecommerce.repository;


import com.learn.ecommerce.entity.OrderItem;
import com.learn.ecommerce.entity.WebOrder;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemsRepo extends JpaRepository<@NotNull OrderItem, @NotNull Long> {

    List<OrderItem> findByWebOrder(WebOrder webOrder);

    Optional<OrderItem> findByIdAndWebOrder(Long itemId, WebOrder webOrder);

	Optional<OrderItem> findByIdAndIsDeleted(Long id, boolean isDeleted);
	List<OrderItem> findAllByIsDeleted(boolean isDeleted);
}