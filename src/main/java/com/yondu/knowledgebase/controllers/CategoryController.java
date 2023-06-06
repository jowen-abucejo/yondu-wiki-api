package com.yondu.knowledgebase.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.category.CategoryDTO;
import com.yondu.knowledgebase.DTO.category.CategoryMapper;
import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.entities.Category;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.Post;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CategoryRepository;
import com.yondu.knowledgebase.services.CategoryService;
import com.yondu.knowledgebase.services.PageService;
import com.yondu.knowledgebase.services.PostService;

import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PageService pageService;

    @Autowired
    private PostService postService;


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
    public ResponseEntity<ApiResponse<CategoryDTO>> editCategory(@RequestBody CategoryDTO categoryDto, @PathVariable Long id) {
        if (categoryService.isCategoryNameTaken(categoryDto.getName())) {
            throw new RequestValidationException("Category name is already taken");
        }
        Category category = categoryService.getCategory(id);
        categoryMapper.updateCategory(categoryDto, category);
        Category updatedCategory = categoryService.editCategory(category);
        CategoryDTO newCategoryDTO = categoryMapper.toDto(updatedCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(newCategoryDTO,"Category has been successfully edited"));
    }

    @PutMapping("/categories/{id}/delete")
    public ResponseEntity<ApiResponse<CategoryDTO>> deleteCategory(@PathVariable Long id) {
        Category category = categoryService.getCategory(id);
        Category updatedCategory = categoryService.deleteCategory(category);
        CategoryDTO newCategoryDTO = categoryMapper.toDto(updatedCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(newCategoryDTO,"Category has been successfully deleted"));
    }

    @GetMapping("/categories")
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/pages/{pageId}/categories")
    public ResponseEntity<ApiResponse<CategoryDTO>> assignPageCategory(@RequestBody CategoryDTO categoryDto, @PathVariable Long pageId) {
        // Retrieve the page by ID using the PageService
        Page page = pageService.getPage(pageId);
        if (page == null) {
            throw new ResourceNotFoundException("Page not found with ID: " + pageId);
        }
    
        // Retrieve the category by ID using the CategoryService
        Category category = categoryService.getCategory(categoryDto.getId());
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryDto.getId());
        }

     
    
        // Add the page to the category's pages list
        category.getPages().add(page);
    
        // Save the updated category
         
        Category updatedCategory = categoryService.addPageCategory(category);

        //Category newCategory = categoryService.getCategory(categoryDto.getId());

        CategoryDTO newCategoryDTO = categoryMapper.toDto(updatedCategory);
    
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(newCategoryDTO, "Category added to page successfully"));


    }


    @PutMapping("/pages/{pageId}/categories/{newCategoryId}")
    public ResponseEntity<ApiResponse<CategoryDTO>> editPageCategory(@RequestBody CategoryDTO categoryDto, @PathVariable Long pageId, @PathVariable Long newCategoryId) {
        // Retrieve the page by ID using the PageService
        Page page = pageService.getPage(pageId);
        if (page == null) {
            throw new ResourceNotFoundException("Page not found with ID: " + pageId);
        }
    
        // Retrieve the category by ID using the CategoryService
        Category category = categoryService.getCategory(newCategoryId);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with ID: " + newCategoryId);
        }

        Category oldCategory = categoryService.getCategory(categoryDto.getId());
    
        // Remove the page from its current categories set
        Set<Category> currentCategories = page.getCategories();
        currentCategories.remove(oldCategory);
    
        // Add the page to the new category's pages list
        category.getPages().add(page);
    
        // Save the updated category and page
        categoryService.editPageCategory(category);
    
        CategoryDTO updatedCategoryDto = categoryMapper.toDto(category);
        return ResponseEntity.ok(ApiResponse.success(updatedCategoryDto, "Page category updated successfully"));
    }


    @PostMapping("/posts/{postId}/categories")
    public ResponseEntity<ApiResponse<CategoryDTO>> assignPostCategory(@RequestBody CategoryDTO categoryDto, @PathVariable Long postId) {
        // Retrieve the page by ID using the PageService
        Post post = postService.getPost(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Page not found with ID: " + postId);
        }
    
        // Retrieve the category by ID using the CategoryService
        Category category = categoryService.getCategory(categoryDto.getId());
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryDto.getId());
        }

     
    
        // Add the page to the category's pages list
        category.getPosts().add(post);
    
        // Save the updated category
         
        Category updatedCategory = categoryService.addPageCategory(category);

        //Category newCategory = categoryService.getCategory(categoryDto.getId());

        CategoryDTO newCategoryDTO = categoryMapper.toDto(updatedCategory);
    
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(newCategoryDTO, "Category added to post successfully"));


    }

}