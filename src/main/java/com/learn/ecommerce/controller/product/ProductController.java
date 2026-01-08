package com.learn.ecommerce.controller.product;

import com.learn.ecommerce.DTO.ProductDTO.*;
import com.learn.ecommerce.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Public endpoint accessible by everyone
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")

    @GetMapping("/get")
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @PageableDefault(page = 0, size = 10) Pageable pageable
            ) {
        return new ResponseEntity<>(productService.getProducts(priceMin,priceMax,sortBy,sortDir,pageable), HttpStatus.OK);
    }

    // Public endpoint to get single product
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        return new ResponseEntity<>(productService.getProduct(id), HttpStatus.OK);
    }

    // Only admins can add a new product
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")

    @PostMapping("/add")
    public ResponseEntity<ProductStatusDTO> addProduct(@Validated @RequestBody AddProductDTO productBody) {
        return new ResponseEntity<>(productService.addProduct(productBody), HttpStatus.OK);
    }

    // Only admins can edit products
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")

    @PutMapping("/{id}")
    public ResponseEntity<ProductStatusDTO> editProduct(
            @PathVariable Long id,
            @Validated @RequestBody AddProductDTO productBody
    ) {
        return new ResponseEntity<>(productService.editProduct(id, productBody), HttpStatus.OK);
    }

    // Only admins can delete products
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductStatusDTO> deleteProduct(@PathVariable Long id) {
        return new ResponseEntity<>(productService.deleteProduct(id), HttpStatus.OK);
    }


    @GetMapping ("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String query) {
        return new ResponseEntity<>(productService.searchProducts(query), HttpStatus.OK);

    }
    @PatchMapping("/update-category/{productId}")
    public ResponseEntity<ProductCategoryDTO> updateProductCategory(
            @PathVariable Long productId,
            @Validated  @RequestParam AddProductCategoryDTO body
    ) {
        return new ResponseEntity<>(productService.updateProductCategory(productId, body), HttpStatus.OK);
    }
}
