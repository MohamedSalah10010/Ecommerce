package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.Inventory;
import org.springframework.data.repository.CrudRepository;

public interface   InventoryRepo extends CrudRepository<Inventory,Long> {
}
