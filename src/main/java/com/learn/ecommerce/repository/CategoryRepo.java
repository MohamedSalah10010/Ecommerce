package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepo extends CrudRepository<Category, Long>
{

}
