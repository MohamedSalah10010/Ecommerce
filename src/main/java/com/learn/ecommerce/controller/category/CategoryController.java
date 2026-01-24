package com.learn.ecommerce.controller.category;

import com.learn.ecommerce.DTO.Category.AddCategoryDTO;
import com.learn.ecommerce.DTO.Category.CategoryDTO;
import com.learn.ecommerce.DTO.Category.CategoryStatusDTO;
import com.learn.ecommerce.DTO.ErrorResponseDTO;
import com.learn.ecommerce.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Getter
@RestController
@RequestMapping("/category")
@Tag(name = "Categories", description = "Category management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // -----------------------------
    // Get all categories (USER, ADMIN)
    // -----------------------------
    @Operation(
            summary = "Get all categories",
            description = "Retrieve a list of all product categories"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    @GetMapping("/get")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        log.info("Fetching all categories");
        List<CategoryDTO> categories = categoryService.getAllCategories();
        log.info("Retrieved {} categories", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // -----------------------------
    // Get category by ID (USER, ADMIN)
    // -----------------------------
    @Operation(
            summary = "Get category by ID",
            description = "Retrieve category details using its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/get/{id}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        log.info("Fetching category with id={}", id);
        CategoryDTO category = categoryService.getCategoryById(id);
        log.info("Category retrieved: {}", category.getName());
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    // -----------------------------
    // Add new category (ADMIN only)
    // -----------------------------
    @Operation(
            summary = "Add new category",
            description = "Create a new product category (Admin only)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/add")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<CategoryStatusDTO> addCategory(@Validated @RequestBody AddCategoryDTO categoryDTO) {
        log.info("Admin adding new category: {}", categoryDTO.getName());
        CategoryStatusDTO status = categoryService.addCategory(categoryDTO);
        log.info("Category added successfully: {}", status.getName());
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    // -----------------------------
    // Update category (ADMIN only)
    // -----------------------------
    @Operation(
            summary = "Update category",
            description = "Update an existing category by ID (Admin only)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid category data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/update/{id}")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<CategoryStatusDTO> updateCategory(
            @PathVariable Long id,
            @Validated @RequestBody AddCategoryDTO categoryDTO
    ) {
        log.info("Admin updating category id={}", id);
        CategoryStatusDTO status = categoryService.updateCategory(id, categoryDTO);
        log.info("Category updated successfully: {}", status.getName());
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    // -----------------------------
    // Delete category (ADMIN only)
    // -----------------------------
    @Operation(
            summary = "Delete category",
            description = "Delete a category by ID (Admin only)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/delete/{id}")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<CategoryStatusDTO> deleteCategory(@PathVariable Long id) {
        log.warn("Admin deleting category id={}", id);
        CategoryStatusDTO status = categoryService.deleteCategory(id);
        log.info("Category deleted successfully: {}", status.getName());
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}