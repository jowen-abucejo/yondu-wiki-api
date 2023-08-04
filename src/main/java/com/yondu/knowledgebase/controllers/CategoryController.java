package com.yondu.knowledgebase.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.category.CategoryDTO;
import com.yondu.knowledgebase.DTO.category.CategoryMapper;
import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.entities.Category;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.Post;
import com.yondu.knowledgebase.entities.User;
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

        if (categoryDto.getName() == null || categoryDto.getName().isEmpty()) {
            throw new RequestValidationException("Category name is required");
        }

        if (categoryService.isCategoryNameTaken(categoryDto.getName())) {
            throw new RequestValidationException("Category name is already taken");
        }

        Category category = categoryMapper.toEntity(categoryDto);
        Category createdCategory = categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdCategory, "Category created successfully"));
    }

    @PutMapping("/categories/{id}/edit")
    public ResponseEntity<ApiResponse<CategoryDTO>> editCategory(@RequestBody CategoryDTO categoryDto,
            @PathVariable Long id) {

        if (categoryDto.getName() == null || categoryDto.getName().isEmpty()) {
            throw new RequestValidationException("Category name is required");
        }
        if (categoryService.isCategoryNameTaken(categoryDto.getName())) {
            throw new RequestValidationException("Category name is already taken");
        }
        Category category = categoryService.getCategory(id);
        categoryMapper.updateCategory(categoryDto, category);
        Category updatedCategory = categoryService.editCategory(category);
        CategoryDTO newCategoryDTO = categoryMapper.toDto(updatedCategory);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newCategoryDTO, "Category has been successfully edited"));
    }

    @PutMapping("/categories/{id}/delete")
    public ResponseEntity<ApiResponse<CategoryDTO>> deleteCategory(@PathVariable Long id) {
        Category category = categoryService.getCategory(id);
        if (category.getDeleted() == true) {
            throw new RequestValidationException("Category is already deleted");
        }
        Category updatedCategory = categoryService.deleteCategory(category);
        CategoryDTO newCategoryDTO = categoryMapper.toDto(updatedCategory);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newCategoryDTO, "Category has been successfully deleted"));
    }

    @PutMapping("/categories/{id}/restore")
    public ResponseEntity<ApiResponse<CategoryDTO>> restoreCategory(@PathVariable Long id) {
        Category category = categoryService.getCategory(id);

        Category updatedCategory = categoryService.restoreCategory(category);
        CategoryDTO newCategoryDTO = categoryMapper.toDto(updatedCategory);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newCategoryDTO, "Category has been successfully restored"));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategory(id);
        CategoryDTO newCategoryDTO = categoryMapper.toDto(category);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(newCategoryDTO, "Category" + id + "found"));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .filter(category -> !category.getDeleted())
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(categoryDTOs, "Success retrieving all categories"));
    }

    @GetMapping("/categories/include-deleted")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategoriesIncludeDeleted() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(categoryDTOs, "Success retrieving all categories"));
    }

    // @PostMapping("/pages/{pageId}/categories")
    // public ResponseEntity<ApiResponse<CategoryDTO>>
    // assignPageCategory(@RequestBody CategoryDTO categoryDto,
    // @PathVariable Long pageId) {
    // // Retrieve the page by ID using the PageService
    // Page page = pageService.getPage(pageId);

    // // Check if the page is locked
    // if (isPageLocked(page)) {
    // return ResponseEntity.status(HttpStatus.FORBIDDEN)
    // .body(ApiResponse.error("Page is currently locked and cannot be edited"));
    // }

    // // Retrieve the category by ID using the CategoryService
    // Category category = categoryService.getCategory(categoryDto.getId());

    // // Check if the page is already assigned to the category
    // boolean pageAlreadyAssigned = category.getPages().stream()
    // .anyMatch(p -> p.getId().equals(page.getId()));

    // if (pageAlreadyAssigned) {
    // return ResponseEntity.badRequest().body(ApiResponse.error("Page is already
    // assigned to the category"));
    // }

    // // Add the page to the category's pages list
    // category.getPages().add(page);

    // // Save the updated category
    // Category updatedCategory = categoryService.addPageCategory(category);

    // CategoryDTO newCategoryDTO = categoryMapper.toDto(updatedCategory);

    // return ResponseEntity.status(HttpStatus.CREATED)
    // .body(ApiResponse.success(newCategoryDTO, "Category added to page
    // successfully"));
    // }

    // private boolean isPageLocked(Page page) {
    // var currentTime = LocalDateTime.now();
    // var currentUser = (User)
    // SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // boolean isSameUser = page.getLockedBy().getId().equals(currentUser.getId());
    // boolean isPageUnlocked = currentTime.isAfter(page.getLockEnd());

    // // Check if the page can be edited by the current user
    // if (!isSameUser && !isPageUnlocked) {
    // return true;
    // }

    // if (!isSameUser) {
    // page.setLockedBy(currentUser);
    // }

    // if (isPageUnlocked) {
    // page.setLockStart(currentTime);
    // }

    // page.setLockEnd(currentTime.plusHours(1));

    // return false;
    // }

    // @PutMapping("/pages/{pageId}/categories/{newCategoryId}")
    // public ResponseEntity<ApiResponse<CategoryDTO>> editPageCategory(@RequestBody
    // CategoryDTO categoryDto,
    // @PathVariable Long pageId, @PathVariable Long newCategoryId) {
    // // Retrieve the page by ID using the PageService
    // Page page = pageService.getPage(pageId);

    // // Check if the page is locked
    // if (isPageLocked(page)) {
    // return ResponseEntity.status(HttpStatus.FORBIDDEN)
    // .body(ApiResponse.error("Page is currently locked and cannot be edited"));
    // }

    // // Retrieve the category by ID using the CategoryService
    // Category category = categoryService.getCategory(newCategoryId);

    // // Check if the page is already assigned to the category
    // boolean pageAlreadyAssigned = category.getPages().stream()
    // .anyMatch(p -> p.getId().equals(page.getId()));

    // if (pageAlreadyAssigned) {
    // return ResponseEntity.badRequest().body(ApiResponse.error("Page is already
    // assigned to the category"));
    // }

    // Category oldCategory = categoryService.getCategory(categoryDto.getId());

    // // Remove the page from its current categories set
    // Set<Category> currentCategories = page.getCategories();
    // currentCategories.remove(oldCategory);

    // // Add the page to the new category's pages list
    // category.getPages().add(page);

    // // Save the updated category and page
    // categoryService.editPageCategory(category);

    // CategoryDTO updatedCategoryDto = categoryMapper.toDto(category);
    // return ResponseEntity.ok(ApiResponse.success(updatedCategoryDto, "Page
    // category updated successfully"));
    // }

    @PostMapping("/posts/{postId}/categories")
    public ResponseEntity<ApiResponse<CategoryDTO>> assignPostCategory(@RequestBody CategoryDTO categoryDto,
            @PathVariable Long postId) {
        // Retrieve the page by ID using the PageService
        Post post = postService.getPost(postId);

        // Retrieve the category by ID using the CategoryService
        Category category = categoryService.getCategory(categoryDto.getId());

        // Check if the post is already assigned to the category
        boolean postAlreadyAssigned = category.getPosts().stream()
                .anyMatch(p -> p.getId().equals(post.getId()));

        if (postAlreadyAssigned) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Post is already assigned to the category"));
        }

        // Add the page to the category's pages list
        category.getPosts().add(post);

        // Save the updated category

        Category updatedCategory = categoryService.addPageCategory(category);

        // Category newCategory = categoryService.getCategory(categoryDto.getId());

        CategoryDTO newCategoryDTO = categoryMapper.toDto(updatedCategory);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newCategoryDTO, "Category added to post successfully"));

    }

    @PutMapping("/posts/{postId}/categories/{newCategoryId}")
    public ResponseEntity<ApiResponse<CategoryDTO>> editPostCategory(@RequestBody CategoryDTO categoryDto,
            @PathVariable Long postId, @PathVariable Long newCategoryId) {
        // Retrieve the page by ID using the PageService
        Post post = postService.getPost(postId);

        // Retrieve the category by ID using the CategoryService
        Category category = categoryService.getCategory(newCategoryId);

        // Check if the post is already assigned to the category
        boolean postAlreadyAssigned = category.getPosts().stream()
                .anyMatch(p -> p.getId().equals(post.getId()));

        if (postAlreadyAssigned) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Post is already assigned to the category"));
        }

        Category oldCategory = categoryService.getCategory(categoryDto.getId());

        // Remove the page from its current categories set
        Set<Category> currentCategories = post.getCategories();
        currentCategories.remove(oldCategory);

        // Add the page to the new category's pages list
        category.getPosts().add(post);

        // Save the updated category and page
        categoryService.editPageCategory(category);

        CategoryDTO updatedCategoryDto = categoryMapper.toDto(category);
        return ResponseEntity.ok(ApiResponse.success(updatedCategoryDto, "Post category updated successfully"));
    }

    @GetMapping("/categories/paginated")
    public ResponseEntity<?> getPaginatedCategory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<CategoryDTO> paginatedCategories = categoryService.getPaginatedCategories(page, size);

        ApiResponse apiResponse = ApiResponse.success(paginatedCategories, "Retrieved categories successfully");
        return ResponseEntity.ok(apiResponse);
    }

}