package com.learn.ecommerce.repository;


import com.learn.ecommerce.model.WebOrder;
import org.springframework.data.repository.CrudRepository;

public interface WebOrderRepo extends CrudRepository<WebOrder,Long> {
}
