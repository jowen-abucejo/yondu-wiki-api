package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.category.CategoryDTO;
import com.yondu.knowledgebase.DTO.tag.TagDTO;
import com.yondu.knowledgebase.DTO.tag.TagMapper;
import com.yondu.knowledgebase.entities.Category;
import com.yondu.knowledgebase.entities.Tag;
import com.yondu.knowledgebase.exceptions.RequestValidationException;
import com.yondu.knowledgebase.repositories.TagRepository;
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

    public TagController(TagMapper tagMapper, TagRepository tagRepository){
        this.tagMapper = tagMapper;
        this.tagRepository = tagRepository;
    }

    @GetMapping("/tags")
    public List<TagDTO> getAllTags(){
        List<Tag> tags = tagService.getAllTags();
        return tags.stream()
                .map(tagMapper::toDto)
                .collect(Collectors.toList());
    }


    @PostMapping("/tags")
    public ResponseEntity<Tag> createTag(@RequestBody TagDTO tagDTO) {
        Tag tag = tagMapper.toEntity(tagDTO);
        Tag createdTag = tagRepository.save(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    @PutMapping("/tags/{id}/edit")
    public ResponseEntity<ApiResponse<Tag>> editTag(@RequestBody TagDTO tagDTO, @PathVariable Long id) {

        Tag tag = tagService.getTag(id);
        tagMapper.updateTag(tagDTO, tag);
        Tag updateTag = tagService.updateTag(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(updateTag,"Tag has been successfully edited"));
    }

    @PutMapping("/tags/{id}/delete")
    public ResponseEntity<ApiResponse<Tag>> deleteTag(@PathVariable Long id){
        Tag tag = tagService.getTag(id);
        Tag deleteTag = tagService.deleteTag(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(deleteTag, "Tag has been deleted successfully"));
    }






}
