package com.learn.ecommerce.services;

import com.learn.ecommerce.model.Product;
import com.learn.ecommerce.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ProductService {

    private ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public Collection<Product> getProducts()
    {
        return (Collection<Product>) productRepo.findAll();
    }
}

