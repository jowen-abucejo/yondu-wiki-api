package com.yondu.knowledgebase.DTO.category;

import org.springframework.stereotype.Component;

import com.yondu.knowledgebase.entities.Category;

@Component
public class CategoryMapper {
    
    public Category toEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    public void updateCategory(CategoryDTO dto, Category category) {
        category.setName(dto.getName());
    }

    public void deleteCategory(CategoryDTO dto, Category category) {
        category.setName(dto.getName());
    }
}
