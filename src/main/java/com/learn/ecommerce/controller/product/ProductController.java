package com.learn.ecommerce.controller.product;

import com.learn.ecommerce.DTO.ErrorResponseDTO;
import com.learn.ecommerce.DTO.ProductDTO.*;
import com.learn.ecommerce.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Product management and browsing APIs")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ------------------------------------------------
    // Get paginated list of products (USER, ADMIN)
    // ------------------------------------------------
    @Operation(summary = "Get products",
            description = "Retrieve paginated list of products with optional price filtering and sorting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/get")
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @Parameter(description = "Minimum product price") @RequestParam(required = false) Double priceMin,
            @Parameter(description = "Maximum product price") @RequestParam(required = false) Double priceMax,
            @Parameter(description = "Sort field", example = "name") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction", example = "asc") @RequestParam(defaultValue = "asc") String sortDir,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        log.info("Fetching products | priceMin={}, priceMax={}, sortBy={}, sortDir={}",
                priceMin, priceMax, sortBy, sortDir);
        return ResponseEntity.ok(productService.getProducts(priceMin, priceMax, sortBy, sortDir, pageable));
    }

    // ------------------------------------------------
    // Get single product by ID (USER, ADMIN)
    // ------------------------------------------------
    @Operation(summary = "Get product by ID", description = "Retrieve product details using product ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id
    ) {
        log.info("Fetching product with id={}", id);
        return ResponseEntity.ok(productService.getProduct(id));
    }

    // ------------------------------------------------
    // Add new product (ADMIN only)
    // ------------------------------------------------
    @Operation(summary = "Add new product", description = "Create a new product (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product added successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ProductStatusDTO> addProduct(@Validated @RequestBody AddProductDTO productBody) {
        log.info("Admin adding new product with name={}", productBody.getName());
        return ResponseEntity.ok(productService.addProduct(productBody));
    }

    // ------------------------------------------------
    // Edit product (ADMIN only)
    // ------------------------------------------------
    @Operation(summary = "Edit product", description = "Update existing product details (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product edited successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductStatusDTO> editProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id,
            @Validated @RequestBody AddProductDTO productBody
    ) {
        log.info("Admin editing product id={}", id);
        return ResponseEntity.ok(productService.editProduct(id, productBody));
    }

    // ------------------------------------------------
    // Delete product (ADMIN only)
    // ------------------------------------------------
    @Operation(summary = "Delete product", description = "Soft delete product by ID (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductStatusDTO> deleteProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id
    ) {
        log.warn("Admin deleting product id={}", id);
        return ResponseEntity.ok(productService.deleteProduct(id));
    }

    // ------------------------------------------------
    // Search products by keyword (USER, ADMIN)
    // ------------------------------------------------
    @Operation(summary = "Search products", description = "Search products by name or description")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products found successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @Parameter(description = "Search keyword", required = true) @RequestParam String query
    ) {
        log.info("Searching products with query='{}'", query);
        return ResponseEntity.ok(productService.searchProducts(query));
    }

    // ------------------------------------------------
    // Update product category (ADMIN only)
    // ------------------------------------------------
    @Operation(summary = "Update product category", description = "Change product category (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/update-category/{productId}")
    public ResponseEntity<ProductCategoryDTO> updateProductCategory(
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId,
            @Validated @RequestParam AddProductCategoryDTO body
    ) {
        log.info("Admin updating category for product id={}", productId);
        return ResponseEntity.ok(productService.updateProductCategory(productId, body));
    }
}
