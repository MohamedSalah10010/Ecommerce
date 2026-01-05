package com.learn.ecommerce.controller.product;

import com.learn.ecommerce.DTO.ProductResponseDTO.ProductDTO;
import com.learn.ecommerce.DTO.ProductResponseDTO.ProductStatusDTO;
import com.learn.ecommerce.DTO.ProductResponseDTO.addProductDTO;
import com.learn.ecommerce.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Public endpoint accessible by everyone
    @GetMapping
    public ResponseEntity<Collection<ProductDTO>> getProducts() {
        return new ResponseEntity<>(productService.getProducts(), HttpStatus.OK);
    }

    // Public endpoint to get single product
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        return new ResponseEntity<>(productService.getProduct(id), HttpStatus.OK);
    }

    // Only admins can add a new product
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductStatusDTO> addProduct(@Validated @RequestBody addProductDTO productBody) {
        return new ResponseEntity<>(productService.addProduct(productBody), HttpStatus.OK);
    }

    // Only admins can edit products
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductStatusDTO> editProduct(
            @PathVariable Long id,
            @Validated @RequestBody addProductDTO productBody
    ) {
        return new ResponseEntity<>(productService.editProduct(id, productBody), HttpStatus.OK);
    }

    // Only admins can delete products
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductStatusDTO> deleteProduct(@PathVariable Long id) {
        return new ResponseEntity<>(productService.deleteProduct(id), HttpStatus.OK);
    }
}
