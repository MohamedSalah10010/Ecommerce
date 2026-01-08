package com.learn.ecommerce.services;

import com.learn.ecommerce.DTO.CategoryDTO.AddCategoryDTO;
import com.learn.ecommerce.DTO.CategoryDTO.CategoryDTO;
import com.learn.ecommerce.DTO.CategoryDTO.CategoryStatusDTO;
import com.learn.ecommerce.entity.Category;
import com.learn.ecommerce.exceptionhandler.CategoryNotFoundException;
import com.learn.ecommerce.repository.CategoryRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@RequiredArgsConstructor
public class CategoryService {

    private CategoryRepo categoryRepo;


    public CategoryDTO getCategoryById(long id) {
        Category category = categoryRepo.findById(id).orElse(null);
        if(category == null || category.isDeleted()  ) {
            throw new CategoryNotFoundException();
        }
        return new CategoryDTO().builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

//    public Category getCategoryByName(@RequestParam String name) {

//    }

//    public List<CategoryDTO> getAllCategories() {
//        return categoryRepo.findAll().stream()
//                .filter(category -> !category.isDeleted())
//                .map(category -> CategoryDTO.builder()
//                        .id(category.getId())
//                        .name(category.getName())
//                        .description(category.getDescription())
//                        .build())
//                .toList();
//    }

    public List<CategoryDTO> getAllCategories() {
        List<CategoryDTO> categoriesDTO = new ArrayList<>() ;
        List<Category> categories = (List<Category>) categoryRepo.findAll();
       if(categories.isEmpty())
       { throw new CategoryNotFoundException(); }
        for (Category category : categories) {
            if (category.isDeleted())
                continue;
            categoriesDTO.add(new CategoryDTO().builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .build());
        }
        return categoriesDTO;
    }

    @Transactional
    public CategoryStatusDTO addCategory( AddCategoryDTO addCategoryDTO) {
       Category category = new Category();
         category.setName(addCategoryDTO.getName());
         category.setDescription(addCategoryDTO.getDescription());
        categoryRepo.save(category);
        return new CategoryStatusDTO().builder()
                .id(category.getId())
                .name(category.getName())
                .statusMessage("Category added successfully")
                .build();
    }

    @Transactional
    public CategoryStatusDTO updateCategory( Long id,AddCategoryDTO addCategoryDTO) {
       Category category= categoryRepo.findById(id).orElseThrow(() -> new CategoryNotFoundException());
       if(category.isDeleted()) {throw new CategoryNotFoundException();}
       if(addCategoryDTO.getName() != null)
            {category.setName(addCategoryDTO.getName());}
       if(addCategoryDTO.getDescription() != null)
            {category.setDescription(addCategoryDTO.getDescription());}
         categoryRepo.save(category);

        return new CategoryStatusDTO().builder()
                .id(category.getId())
                .name(category.getName())
                .statusMessage("Category updated successfully")
                .build();
    }

    @Transactional
    public CategoryStatusDTO deleteCategory( Long id) {
       Category category= categoryRepo.findById(id).orElseThrow(() -> new CategoryNotFoundException());
       if(category.isDeleted()) {throw new CategoryNotFoundException();}
       category.setDeleted(true);
       categoryRepo.save(category);
        return new CategoryStatusDTO().builder()
                .id(category.getId())
                .name(category.getName())
                .statusMessage("Category deleted successfully")
                .build();
    }

}
