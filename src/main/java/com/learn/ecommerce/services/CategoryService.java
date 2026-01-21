package com.learn.ecommerce.services;

import com.learn.ecommerce.DTO.CategoryDTO.AddCategoryDTO;
import com.learn.ecommerce.DTO.CategoryDTO.CategoryDTO;
import com.learn.ecommerce.DTO.CategoryDTO.CategoryStatusDTO;
import com.learn.ecommerce.entity.Category;
import com.learn.ecommerce.exceptionhandler.CategoryNotFoundException;
import com.learn.ecommerce.repository.CategoryRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class CategoryService {

    private CategoryRepo categoryRepo;

    public CategoryDTO getCategoryById(long id) {
        Category category = categoryRepo.findById(id).orElse(null);
        if (category == null || category.isDeleted()) {
            log.warn("Category with id {} not found or deleted", id);
            throw new CategoryNotFoundException();
        }
        log.info("Fetched category with id {}", id);
        return new CategoryDTO().builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    public List<CategoryDTO> getAllCategories() {
        List<CategoryDTO> categoriesDTO = new ArrayList<>();
        List<Category> categories = (List<Category>) categoryRepo.findAll();
        if (categories.isEmpty()) {
            log.warn("No categories found in database");
            throw new CategoryNotFoundException();
        }
        for (Category category : categories) {
            if (category.isDeleted())
                continue;
            categoriesDTO.add(new CategoryDTO().builder()
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
        return new CategoryStatusDTO().builder()
                .id(category.getId())
                .name(category.getName())
                .statusMessage("Category added successfully")
                .build();
    }

    @Transactional
    public CategoryStatusDTO updateCategory(Long id, AddCategoryDTO addCategoryDTO) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> {
            log.warn("Category with id {} not found for update", id);
            return new CategoryNotFoundException();
        });
        if (category.isDeleted()) {
            log.warn("Category with id {} is deleted and cannot be updated", id);
            throw new CategoryNotFoundException();
        }
        if (addCategoryDTO.getName() != null) category.setName(addCategoryDTO.getName());
        if (addCategoryDTO.getDescription() != null) category.setDescription(addCategoryDTO.getDescription());
        categoryRepo.save(category);
        log.info("Updated category: {} (id: {})", category.getName(), category.getId());
        return new CategoryStatusDTO().builder()
                .id(category.getId())
                .name(category.getName())
                .statusMessage("Category updated successfully")
                .build();
    }

    @Transactional
    public CategoryStatusDTO deleteCategory(Long id) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> {
            log.warn("Category with id {} not found for deletion", id);
            return new CategoryNotFoundException();
        });
        if (category.isDeleted()) {
            log.warn("Category with id {} is already deleted", id);
            throw new CategoryNotFoundException();
        }
        category.setDeleted(true);
        categoryRepo.save(category);
        log.info("Deleted category: {} (id: {})", category.getName(), category.getId());
        return new CategoryStatusDTO().builder()
                .id(category.getId())
                .name(category.getName())
                .statusMessage("Category deleted successfully")
                .build();
    }

}
