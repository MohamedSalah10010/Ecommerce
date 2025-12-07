package com.learn.ecommerce.repository;


import com.learn.ecommerce.model.OrderQuantities;
import org.springframework.data.repository.CrudRepository;

public interface OrderQuantitiesRepo extends CrudRepository<OrderQuantities,Long> {
}
