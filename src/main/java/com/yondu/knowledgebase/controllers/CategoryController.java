package com.yondu.knowledgebase.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.category.CategoryDTO;
import com.yondu.knowledgebase.DTO.category.CategoryMapper;
import com.yondu.knowledgebase.entities.Category;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CategoryRepository;
import com.yondu.knowledgebase.services.CategoryService;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryMapper categoryMapper, CategoryRepository categoryRepository) {
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody CategoryDTO categoryDto) {
    
            if(categoryDto.getName()==null || categoryDto.getName().isEmpty()){
                throw new RequestValidationException("Category name is required");
            }

            if (categoryService.isCategoryNameTaken(categoryDto.getName())) {
                throw new RequestValidationException("Category name is already taken");
            }
            
            Category category = categoryMapper.toEntity(categoryDto);
            Category createdCategory = categoryRepository.save(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdCategory, "Category created successfully"));     
    }

    @PutMapping("/categories/{id}/edit")
    public ResponseEntity<ApiResponse<Category>> editCategory(@RequestBody CategoryDTO categoryDto, @PathVariable Long id) {
        if (categoryService.isCategoryNameTaken(categoryDto.getName())) {
            throw new RequestValidationException("Category name is already taken");
        }
        Category category = categoryService.getCategory(id);
        categoryMapper.updateCategory(categoryDto, category);
        Category updatedCategory = categoryService.editCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(updatedCategory,"Category has been successfully edited"));
    }

    @PutMapping("/categories/{id}/delete")
    public ResponseEntity<ApiResponse<Category>> deleteCategory(@PathVariable Long id) {
        Category category = categoryService.getCategory(id);
        Category updatedCategory = categoryService.deleteCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(updatedCategory,"Category has been successfully deleted"));
    }

    @GetMapping("/categories")
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
/* 
    @PutMapping("/pages/{pageId}/categories")
    public ResponseEntity<ApiResponse<List<Category>>> assignPageCategory(@RequestBody List<CategoryDTO> categoryDtoList, @PathVariable Long pageId) {
        // Retrieve the page by ID using a PageService or repository
        Page page = pageService.getPage(pageId);
    
        // Create a list to store the assigned categories
        List<Category> assignedCategories = new ArrayList<>();
    
        // Iterate through the CategoryDTO list and assign the page ID to each category
        for (CategoryDTO categoryDto : categoryDtoList) {
            // Check if the category exists in the database by ID
            Category category = categoryService.getCategory(categoryDto.getId());
            
            // Assign the page to the category's pages list
            category.getPages().add(page);
    
            // Save the category with the updated pages list
            Category assignedCategory = categoryService.editCategory(category);
    
            // Add the assigned category to the list
            assignedCategories.add(assignedCategory);
        }
  
      // Return the assigned categories in the response
      return ResponseEntity.ok(ApiResponse.success(assignedCategories, "Page categories assigned successfully"));
  }
*/


}
