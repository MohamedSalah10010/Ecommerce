package com.learn.ecommerce.repository;


import com.learn.ecommerce.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepo extends CrudRepository<Product,Long> {
}
