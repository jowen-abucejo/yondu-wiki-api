package com.yondu.knowledgebase.services;

import org.springframework.beans.factory.annotation.Autowired;
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
        return categoryRepository.save(category);

    }



}
