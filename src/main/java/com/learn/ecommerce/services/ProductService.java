package com.learn.ecommerce.services;

import com.learn.ecommerce.DTO.ProductDTO.*;
import com.learn.ecommerce.entity.Category;
import com.learn.ecommerce.entity.Inventory;
import com.learn.ecommerce.entity.Product;
import com.learn.ecommerce.exceptionhandler.CategoryNotFoundException;
import com.learn.ecommerce.exceptionhandler.ProductNotFoundException;
import com.learn.ecommerce.repository.CategoryRepo;
import com.learn.ecommerce.repository.InventoryRepo;
import com.learn.ecommerce.repository.JpaQueryLogic.ProductSpecification;
import com.learn.ecommerce.repository.ProductRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;
    private final InventoryRepo inventoryRepo;
    private final CategoryRepo categoryRepo;

    public Page<ProductDTO> getProducts(
            Double priceMin,
            Double priceMax,
            String sortBy,
            String direction,
            Pageable pageable
    ) {
        log.info("Fetching products with priceMin={}, priceMax={}, sortBy={}, direction={}, page={}",
                priceMin, priceMax, sortBy, direction, pageable.getPageNumber());

        Specification<Product> spec = Specification
                .where(ProductSpecification.isNotDeleted())
                .and(ProductSpecification.priceBetween(priceMin, priceMax));

        Sort sort = Sort.unsorted();
        if (sortBy != null) {
            sort = Sort.by(
                    "desc".equalsIgnoreCase(direction)
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC,
                    sortBy
            );
        }

        Pageable finalPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<ProductDTO> result = productRepo.findAll(spec, finalPageable)
                .map(product -> ProductDTO.builder()
                        .name(product.getName())
                        .price(product.getPrice())
                        .shortDescription(product.getShortDescription())
                        .build());

        log.info("Fetched {} products", result.getContent().size());
        return result;
    }

    public ProductDTO getProduct(Long id) {
        log.info("Fetching product with ID={}", id);

        Product product = productRepo.findById(id).orElseThrow(() -> {
            log.warn("Product with ID={} not found", id);
            return new ProductNotFoundException();
        });

        if (product.isDeleted()) {
            log.warn("Product with ID={} is marked as deleted", id);
            throw new ProductNotFoundException();
        }

        log.info("Product found: {}", product.getName());
        return ProductDTO.builder()
                .price(product.getPrice())
                .name(product.getName())
                .shortDescription(product.getShortDescription())
                .longDescription(product.getLongDescription())
                .quantity(product.getInventory().getQuantity())
                .categoryName(product.getCategory().getName())
                .categoryId(product.getCategory().getId())
                .build();
    }

    @Transactional
    public ProductStatusDTO addProduct(AddProductDTO productBody) {
        log.info("Adding new product: {}", productBody.getName());

        Product product = new Product();
        product.setName(productBody.getName());
        product.setShortDescription(productBody.getShortDescription());
        product.setLongDescription(productBody.getLongDescription());
        product.setPrice(productBody.getPrice());

        if (productBody.getCategoryId() != null) {
            Category category = categoryRepo.findById(productBody.getCategoryId())
                    .orElseThrow(() -> {
                        log.warn("Category with ID={} not found", productBody.getCategoryId());
                        return new CategoryNotFoundException();
                    });
            product.setCategory(category);
            log.info("Assigned category {} to product {}", category.getName(), product.getName());
        }

        Product savedProduct = productRepo.save(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setQuantity(productBody.getQuantity());
        inventoryRepo.save(inventory);

        log.info("Product {} added successfully with ID={}", savedProduct.getName(), savedProduct.getId());
        return ProductStatusDTO.builder()
                .statusMessage("Product added successfully")
                .productId(savedProduct.getId())
                .productName(savedProduct.getName())
                .build();
    }

    @Transactional
    public ProductStatusDTO editProduct(Long id, AddProductDTO productBody) {
        log.info("Editing product ID={}", id);

        Product product = productRepo.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> {
                    log.warn("Product ID={} not found or deleted", id);
                    return new ProductNotFoundException();
                });

        if (productBody.getName() != null && !productBody.getName().isEmpty()) {
            log.info("Updating name from {} to {}", product.getName(), productBody.getName());
            product.setName(productBody.getName());
        }

        if (productBody.getShortDescription() != null) product.setShortDescription(productBody.getShortDescription());
        if (productBody.getLongDescription() != null) product.setLongDescription(productBody.getLongDescription());
        if (productBody.getPrice() != null) product.setPrice(productBody.getPrice());
        if (productBody.getQuantity() != null) product.getInventory().setQuantity(productBody.getQuantity());

        if (productBody.getCategoryId() != null) {
            Category category = categoryRepo.findById(productBody.getCategoryId())
                    .orElseThrow(() -> {
                        log.warn("Category ID={} not found", productBody.getCategoryId());
                        return new CategoryNotFoundException();
                    });
            product.setCategory(category);
            log.info("Updated product category to {}", category.getName());
        }

        productRepo.save(product);
        log.info("Product ID={} edited successfully", id);

        return ProductStatusDTO.builder()
                .statusMessage("Product Edited successfully")
                .productId(product.getId())
                .productName(product.getName())
                .build();
    }

    @Transactional
    public ProductStatusDTO deleteProduct(Long id) {
        log.info("Deleting product ID={}", id);

        productRepo.findById(id).ifPresentOrElse(product -> {
            product.setDeleted(true);
            productRepo.save(product);
            log.info("Product ID={} marked as deleted", id);
        }, () -> {
            log.warn("Product ID={} not found for deletion", id);
            throw new ProductNotFoundException();
        });

        return ProductStatusDTO.builder()
                .statusMessage("Product Deleted successfully")
                .productId(id)
                .build();
    }

    public List<ProductDTO> searchProducts(String query) {
        log.info("Searching products with query='{}'", query);

        List<ProductDTO> productsDTO = new ArrayList<>();
        List<Product> products = productRepo.searchByName(query);

        for (Product product : products) {
            if (product.isDeleted()) continue;

            productsDTO.add(ProductDTO.builder()
                    .price(product.getPrice())
                    .name(product.getName())
                    .shortDescription(product.getShortDescription())
                    .longDescription(product.getLongDescription())
                    .quantity(product.getInventory().getQuantity())
                    .categoryId(product.getCategory().getId())
                    .categoryName(product.getCategory().getName())
                    .build());
        }

        log.info("Search returned {} products", productsDTO.size());
        return productsDTO;
    }

    @Transactional
    public ProductCategoryDTO updateProductCategory(Long productId, AddProductCategoryDTO body) {
        log.info("Updating category of product ID={} to category ID={}", productId, body.getCategoryId());

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product ID={} not found", productId);
                    return new ProductNotFoundException();
                });

        if (product.isDeleted()) {
            log.warn("Product ID={} is deleted", productId);
            throw new ProductNotFoundException();
        }

        Category category = categoryRepo.findById(body.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("Category ID={} not found", body.getCategoryId());
                    return new CategoryNotFoundException();
                });

        if (category.isDeleted()) {
            log.warn("Category ID={} is deleted", body.getCategoryId());
            throw new CategoryNotFoundException();
        }

        product.setCategory(category);
        productRepo.save(product);

        log.info("Product ID={} category updated to {}", productId, category.getName());

        return ProductCategoryDTO.builder()
                .productName(product.getName())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .productId(product.getId())
                .build();
    }
}
