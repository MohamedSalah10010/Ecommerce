package com.learn.ecommerce.controller;


import com.learn.ecommerce.DTO.CategoryDTO.AddCategoryDTO;
import com.learn.ecommerce.DTO.CategoryDTO.CategoryDTO;
import com.learn.ecommerce.DTO.CategoryDTO.CategoryStatusDTO;
import com.learn.ecommerce.services.CategoryService;
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

public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @GetMapping("/get")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public  ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return new ResponseEntity<>(categoryService.getCategoryById(id), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<CategoryStatusDTO> addCategory( @Validated @RequestBody AddCategoryDTO categoryDTO) {
        return new ResponseEntity<>(categoryService.addCategory(categoryDTO), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryStatusDTO> updateCategory(@PathVariable Long id,
                                                            @Validated @RequestBody AddCategoryDTO categoryDTO) {
        return new ResponseEntity<>(categoryService.updateCategory(id, categoryDTO), HttpStatus.OK);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CategoryStatusDTO> deleteCategory(@PathVariable Long id) {
        return new ResponseEntity<>(categoryService.deleteCategory(id), HttpStatus.OK);
    }

}
