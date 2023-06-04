package com.yondu.knowledgebase.DTO.category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.entities.Category;
import com.yondu.knowledgebase.entities.Page;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    public CategoryDTO toDto(Category category) {

        Long oldPageId = 0L;
        CategoryDTO categoryDto = new CategoryDTO();
        categoryDto.setName(category.getName());
        categoryDto.setId(category.getId());
        categoryDto.setDeleted(category.getDeleted());
        List<PageDTO> pages = new ArrayList<>();
        // Assuming the PageDTO has the necessary fields to create a Page entity
        for (Page page : category.getPages()) {
            if (page.getId() != oldPageId) {
                oldPageId = page.getId();
                pages.add(PageDTO.builder().id(oldPageId).build());

            }

        }
        categoryDto.setPages(pages);
        return categoryDto;
    }

    public Category pageCategoryEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDeleted(dto.getDeleted());

        List<Page> pages = new ArrayList<>();
        // Assuming the PageDTO has the necessary fields to create a Page entity
        for (PageDTO pageDTO : dto.getPages()) {
            Page page = new Page();
            page.setId(pageDTO.getId());
            pages.add(page);
        }
        category.setPages(pages);

        return category;
    }

    public void updateCategory(CategoryDTO dto, Category category) {
        category.setName(dto.getName());
    }

    public void deleteCategory(CategoryDTO dto, Category category) {
        category.setName(dto.getName());
    }
}
