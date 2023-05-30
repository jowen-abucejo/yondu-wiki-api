package com.yondu.knowledgebase.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.yondu.knowledgebase.DTO.category.CategoryDTO;
import com.yondu.knowledgebase.DTO.category.CategoryMapper;
import com.yondu.knowledgebase.entities.Category;
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

    @PostMapping("/category")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDTO categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        Category createdCategory = categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/category/{id}/edit")
    public ResponseEntity<Category> editCategory(@RequestBody CategoryDTO categoryDto, @PathVariable Long id) {
        Category category = categoryService.getCategory(id);
        categoryMapper.updateCategory(categoryDto, category);
        Category updatedCategory = categoryService.editCategory(category);
        return ResponseEntity.ok(updatedCategory);
    }

    @PutMapping("/category/{id}/delete")
    public ResponseEntity<Category> deleteCategory(@PathVariable Long id) {
        Category category = categoryService.getCategory(id);
        Category updatedCategory = categoryService.deleteCategory(category);
        return ResponseEntity.ok(updatedCategory);
    }

}
