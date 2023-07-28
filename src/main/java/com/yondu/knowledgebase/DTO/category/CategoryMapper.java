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

    public CategoryDTO toDto(Category category) {

        CategoryDTO categoryDto = new CategoryDTO();
        categoryDto.setName(category.getName());
        categoryDto.setId(category.getId());
        categoryDto.setDeleted(category.getDeleted());

        return categoryDto;
    }

    public Category pageCategoryEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDeleted(dto.getDeleted());

        return category;
    }

    public void updateCategory(CategoryDTO dto, Category category) {
        category.setName(dto.getName());
    }

    public void deleteCategory(CategoryDTO dto, Category category) {
        category.setName(dto.getName());
    }
}
