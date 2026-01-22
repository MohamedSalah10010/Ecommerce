package com.learn.ecommerce.repository;

import com.learn.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long>
{

}
