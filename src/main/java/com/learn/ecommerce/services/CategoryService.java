package com.learn.ecommerce.services;

import com.learn.ecommerce.DTO.Category.AddCategoryDTO;
import com.learn.ecommerce.DTO.Category.CategoryDTO;
import com.learn.ecommerce.DTO.Category.CategoryStatusDTO;
import com.learn.ecommerce.entity.Category;
import com.learn.ecommerce.exceptionhandler.CategoryNotFoundException;
import com.learn.ecommerce.repository.CategoryRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Getter
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;

    public CategoryDTO getCategoryById(long id) {
        Category category = categoryRepo.findById(id).orElse(null);
        if (category == null || category.isDeleted()) {
            log.warn("Category with id {} not found or deleted", id);
            throw new CategoryNotFoundException("Category Not Found");
        }
        log.info("Fetched category with id {}", id);

	    return  CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    public List<CategoryDTO> getAllCategories() {
        List<CategoryDTO> categoriesDTO = new ArrayList<>();
        List<Category> categories =  categoryRepo.findAll();
//
        for (Category category : categories) {
            if (category.isDeleted())
                continue;
	        new CategoryDTO();
	        categoriesDTO.add(CategoryDTO.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .build());
        }
        log.info("Fetched {} categories", categoriesDTO.size());
        return categoriesDTO;
    }

    @Transactional
    public CategoryStatusDTO addCategory(AddCategoryDTO addCategoryDTO) {
        Category category = new Category();
        category.setName(addCategoryDTO.getName());
        category.setDescription(addCategoryDTO.getDescription());
        categoryRepo.save(category);
        log.info("Added new category: {} (id: {})", category.getName(), category.getId());

	    return CategoryStatusDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .statusMessage("Category added successfully")
                .build();
    }

    @Transactional
    public CategoryStatusDTO updateCategory(Long id, AddCategoryDTO addCategoryDTO) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> {
            log.warn("Category with id {} not found for update", id);
            return new CategoryNotFoundException("Category Not Found");
        });
        if (category.isDeleted()) {
            log.warn("Category with id {} is deleted and cannot be updated", id);
            throw new CategoryNotFoundException("Category Not Found");
        }
        if (addCategoryDTO.getName() != null) category.setName(addCategoryDTO.getName());
        if (addCategoryDTO.getDescription() != null) category.setDescription(addCategoryDTO.getDescription());
        categoryRepo.save(category);
        log.info("Updated category: {} (id: {})", category.getName(), category.getId());


	    return CategoryStatusDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .statusMessage("Category updated successfully")
                .build();
    }

    @Transactional
    public CategoryStatusDTO deleteCategory(Long id) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> {
            log.warn("Category with id {} not found for deletion", id);
            return new CategoryNotFoundException("Category Not Found");
        });
        if (category.isDeleted()) {
            log.warn("Category with id {} is already deleted", id);
            throw new CategoryNotFoundException("Category Not Found");
        }
        category.setDeleted(true);
        categoryRepo.save(category);
        log.info("Deleted category: {} (id: {})", category.getName(), category.getId());
	    return CategoryStatusDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .statusMessage("Category deleted successfully")
                .build();
    }

}