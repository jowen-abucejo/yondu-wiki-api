package com.yondu.knowledgebase.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import com.yondu.knowledgebase.entities.Category;
import com.yondu.knowledgebase.services.CategoryService;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @PostMapping("/category")
    public Category addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

    @PutMapping("/category/{id}/edit")
    public Category editCategory (@PathVariable Long id, @RequestBody String newName){
        Category newCategory = categoryService.getCategory(id);
        newCategory.setName(newName);
        return categoryService.editCategory(newCategory);
    }



    
}
