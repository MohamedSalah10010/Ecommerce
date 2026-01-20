package com.learn.ecommerce.repository;


import com.learn.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepo extends CrudRepository<Product,Long>, JpaSpecificationExecutor<Product> {


    @Query("""
                SELECT p FROM Product p
                WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) AND p.isDeleted = false 
                OR  LOWER(p.shortDescription) LIKE  LOWER(CONCAT('%',:query,'%'))AND p.isDeleted = false
                ORDER BY p.name ASC
          """

    )
    List<Product> searchByName(@Param("query") String query);

    Optional<Product> findByIdAndIsDeletedFalse(Long productId);

}
