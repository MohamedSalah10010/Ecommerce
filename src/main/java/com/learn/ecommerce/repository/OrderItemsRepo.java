package com.learn.ecommerce.repository;


import com.learn.ecommerce.entity.OrderItem;
import com.learn.ecommerce.entity.WebOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemsRepo extends CrudRepository<OrderItem,Long> {

    List<OrderItem> findByWebOrder(WebOrder webOrder);

    Optional<OrderItem> findByIdAndWebOrder(Long itemId, WebOrder webOrder);
}
