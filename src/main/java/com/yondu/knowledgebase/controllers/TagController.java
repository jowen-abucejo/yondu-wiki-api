package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.tag.TagDTO;
import com.yondu.knowledgebase.DTO.tag.TagMapper;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.repositories.TagRepository;
import com.yondu.knowledgebase.services.PostService;
import com.yondu.knowledgebase.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TagController {

    @Autowired
    private TagService tagService;

    private final TagMapper tagMapper;
    private final TagRepository tagRepository;

    @Autowired
    private PostService postService;

    public TagController(TagMapper tagMapper, TagRepository tagRepository) {
        this.tagMapper = tagMapper;
        this.tagRepository = tagRepository;
    }

    @GetMapping("/tags")
    public List<TagDTO> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return tags.stream()
                .map(tagMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/tags")
    public ResponseEntity<ApiResponse<Tag>> createTag(@RequestBody TagDTO tagDTO) {

        if (tagDTO.getName() == null || tagDTO.getName().isEmpty()) {
            throw new RequestValidationException("Tag name is required");
        }
        if (tagService.isTagNameTaken(tagDTO.getName())) {
            throw new RequestValidationException("Tag name already exist");
        }

        Tag tag = tagMapper.toEntity(tagDTO);
        Tag createdTag = tagRepository.save(tag);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdTag, "Tag created successfully"));
    }

    @PutMapping("/tags/{id}/edit")
    public ResponseEntity<ApiResponse<TagDTO>> editTag(@RequestBody TagDTO tagDTO, @PathVariable Long id) {

        if (tagService.isTagNameTaken(tagDTO.getName())) {
            throw new RequestValidationException("Tag name already exist");
        }

        Tag tag = tagService.getTag(id);
        tagMapper.updateTag(tagDTO, tag);
        Tag updateTag = tagService.updateTag(tag);
        TagDTO newTagDTO = tagMapper.toDto(updateTag);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newTagDTO, "Tag has been successfully edited"));
    }

    @PutMapping("/tags/{id}/delete")
    public ResponseEntity<ApiResponse<TagDTO>> deleteTag(@PathVariable Long id) {
        Tag tag = tagService.getTag(id);
        if (tag.getDeleted() == true) {
            throw new RequestValidationException("Tag already deleted");
        }
        Tag deleteTag = tagService.deleteTag(tag);
        TagDTO newTagDTO = tagMapper.toDto(deleteTag);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newTagDTO, "Tag has been deleted successfully"));
    }

    // @PostMapping("/pages/{pageId}/tags")
    // public ResponseEntity<ApiResponse<TagDTO>> assignPageTag(@RequestBody TagDTO
    // tagDTO,
    // @PathVariable Long pageId) {
    // // Retrieve the page by ID using the PageService
    // Page page = pageService.getPage(pageId);

    // // Check if the page is locked
    // if (isPageLocked(page)) {
    // return ResponseEntity.status(HttpStatus.FORBIDDEN)
    // .body(ApiResponse.error("Page is currently locked and cannot be edited"));
    // }

    // // Retrieve the tag by ID using the TagService
    // Tag tag = tagService.getTag(tagDTO.getId());

    // // Check if the page is already assigned to tag
    // boolean pageAlreadyAssigned = tag.getPages().stream()
    // .anyMatch(p -> p.getId().equals(page.getId()));

    // if (pageAlreadyAssigned) {
    // return ResponseEntity.badRequest().body(ApiResponse.error("Page is already
    // assigned to the tag"));
    // }

    // // Add the page to the tag's pages list
    // tag.getPages().add(page);

    // // Save the updated tag
    // Tag updatedTag = tagService.addPageTag(tag);

    // TagDTO newTagDTO = tagMapper.toDto(updatedTag);

    // return ResponseEntity.status(HttpStatus.CREATED)
    // .body(ApiResponse.success(newTagDTO, "Tag added to page successfully"));
    // }

    @PostMapping("/posts/{postId}/tags")
    public ResponseEntity<ApiResponse<TagDTO>> assignPostTag(@RequestBody TagDTO tagDTO,
            @PathVariable Long postId) {
        // Retrieve the page by ID using the PageService
        Post post = postService.getPost(postId);

        // Retrieve the tag by ID using the TagService
        Tag tag = tagService.getTag(tagDTO.getId());

        // Check if the post is already assigned to the tag
        boolean postAlreadyAssigned = tag.getPosts().stream()
                .anyMatch(p -> p.getId().equals(post.getId()));

        if (postAlreadyAssigned) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Post is already assigned to the tag"));
        }

        // Add the page to the tag's pages list
        tag.getPosts().add(post);

        // Save the updated tag

        Tag updateTag = tagService.addPageTag(tag);

        TagDTO newTagDTO = tagMapper.toDto(updateTag);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(newTagDTO, "Tag added to post successfully"));

    }

}
