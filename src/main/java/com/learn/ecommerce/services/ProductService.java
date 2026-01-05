package com.learn.ecommerce.services;

import com.learn.ecommerce.DTO.ProductResponseDTO.ProductDTO;
import com.learn.ecommerce.DTO.ProductResponseDTO.ProductStatusDTO;
import com.learn.ecommerce.DTO.ProductResponseDTO.addProductDTO;
import com.learn.ecommerce.entity.Inventory;
import com.learn.ecommerce.entity.Product;
import com.learn.ecommerce.exceptionhandler.ProductNotFoundException;
import com.learn.ecommerce.repository.ProductRepo;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@Getter
public class ProductService {

    private ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public Collection<ProductDTO> getProducts() {
        Collection<ProductDTO> productsDTO = new ArrayList<ProductDTO>();
        Collection<Product> products = (Collection<Product>) productRepo.findAll();
        for (Product product : products) {
            if (product.isDeleted())
                continue;

            productsDTO.add(new ProductDTO().builder()
                    .price(product.getPrice())
                    .name(product.getName())
                    .shortDescription(product.getShortDescription())
                    .longDescription(product.getLongDescription())
                    .inventory(product.getInventory())
                    .build());
        }
        return productsDTO;
    }

    public ProductDTO getProduct(Long id) {
        Product product = productRepo.findById(id).get();

        if (product == null || product.isDeleted())
            throw new ProductNotFoundException();

        return new ProductDTO().builder()
                .price(product.getPrice())
                .name(product.getName())
                .shortDescription(product.getShortDescription())
                .longDescription(product.getLongDescription())
                .inventory(product.getInventory())
                .build();
    }

    @Transactional
    public ProductStatusDTO addProduct(addProductDTO productBody) {
        Product product = new Product().builder()
                .price(productBody.getPrice())
                .name(productBody.getName())
                .shortDescription(productBody.getShortDescription())
                .longDescription(productBody.getLongDescription())
                .inventory(new Inventory().builder()
                        .quantity(productBody.getQuantity())
                        .build())
                .build();

        productRepo.save(product);

        return new ProductStatusDTO().builder()
                .statusMessage("Product added successfully")
                .productId(product.getId())
                .productName(product.getName())
                .build();
    }

    @Transactional
    public ProductStatusDTO editProduct(Long id, addProductDTO productBody) {
        Optional<Product> productOptional = productRepo.findById(id);
        if (productOptional.isPresent() && !productOptional.get().isDeleted()) {
            Product product = productOptional.get();
            if (!productBody.getName().isEmpty())
                product.setName(productBody.getName());

            if (!productBody.getShortDescription().isEmpty())
                product.setShortDescription(productBody.getShortDescription());

            if (!productBody.getLongDescription().isEmpty())
                product.setLongDescription(productBody.getLongDescription());

            if (!productBody.getPrice().toString().isEmpty())
                product.setPrice(productBody.getPrice());

            if (!productBody.getQuantity().toString().isEmpty())
                product.getInventory().setQuantity(productBody.getQuantity());

            productRepo.save(product);

            return new ProductStatusDTO().builder()
                    .statusMessage("Product Edited successfully")
                    .productId(product.getId())
                    .productName(product.getName())
                    .build();
        }
        throw new ProductNotFoundException();


    }

    @Transactional
    public ProductStatusDTO deleteProduct(Long id) {
        productRepo.findById(id).ifPresentOrElse(product -> {
            product.setDeleted(true);
            productRepo.save(product);
        }, () -> {
            throw new ProductNotFoundException();
        });
        return new ProductStatusDTO().builder()
                .statusMessage("Product Deleted successfully")
                .productId(id)
                .build();
    }
}

