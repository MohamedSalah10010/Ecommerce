package com.learn.ecommerce.services;

import com.learn.ecommerce.DTO.ProductResponseDTO.ProductDTO;
import com.learn.ecommerce.DTO.ProductResponseDTO.ProductStatusDTO;
import com.learn.ecommerce.DTO.ProductResponseDTO.addProductDTO;
import com.learn.ecommerce.entity.Inventory;
import com.learn.ecommerce.entity.Product;
import com.learn.ecommerce.exceptionhandler.ProductNotFoundException;
import com.learn.ecommerce.repository.InventoryRepo;
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
    private InventoryRepo inventoryRepo;
    public ProductService(ProductRepo productRepo, InventoryRepo inventoryRepo) {
        this.productRepo = productRepo;
        this.inventoryRepo = inventoryRepo;
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
        Product product = new Product();
        product.setName(productBody.getName());
        product.setShortDescription(productBody.getShortDescription());
        product.setLongDescription(productBody.getLongDescription());
        product.setPrice(productBody.getPrice());

        Product savedProduct = productRepo.save(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setQuantity(productBody.getQuantity());

        inventoryRepo.save(inventory);

        return new ProductStatusDTO().builder()
                .statusMessage("Product added successfully")
                .productId(savedProduct.getId())
                .productName(savedProduct.getName())
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

