package com.yondu.knowledgebase.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.yondu.knowledgebase.DTO.category.CategoryDTO;
import com.yondu.knowledgebase.entities.Category;
import com.yondu.knowledgebase.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;

    public Category addCategory(Category category){
        categoryRepository.save(category);

        return category;
    }


    public Category getCategory(Long id){
        return categoryRepository.findById(id).orElseThrow();
    }

   
    public Category editCategory(Category category){
        Category existingCategory = getCategory(category.getId());
        existingCategory.setName(category.getName());
        return categoryRepository.save(existingCategory);
    }

     
    public Category deleteCategory(Category category){
       category.setDeleted(true);
       categoryRepository.save(category);
        return category;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public boolean isCategoryNameTaken(String categoryName) {
        return categoryRepository.existsByName(categoryName);
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow();
    }

    public Category addPageCategory(Category category) {

        // Save the updated category
        Category updatedCategory = categoryRepository.save(category);

        return updatedCategory;
    
    }
}
