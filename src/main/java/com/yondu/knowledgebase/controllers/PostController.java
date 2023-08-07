package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.post.PostDTO;
import com.yondu.knowledgebase.DTO.post.PostRequestDTO;
import com.yondu.knowledgebase.DTO.post.PostSearchResult;
import com.yondu.knowledgebase.services.PageService;
import com.yondu.knowledgebase.services.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private final PostService postService;
    private final PageService pageService;

    public PostController(PostService postService, PageService pageService) {
        this.postService = postService;
        this.pageService = pageService;
    }

    @GetMapping("/posts")
    public PaginatedResponse<PageDTO> searchPages(
            @RequestParam(defaultValue = "", name = "key") String searchKey,
            @RequestParam(defaultValue = "", name = "categories") String[] categories,
            @RequestParam(defaultValue = "", name = "tags") String[] tags,
            @RequestParam(defaultValue = "0", name = "archived") Boolean archived,
            @RequestParam(defaultValue = "1", name = "published") Boolean published,
            @RequestParam(defaultValue = "0", name = "exactSearch") Boolean exactSearch,
            @RequestParam(defaultValue = "1", name = "page") int pageNumber,
            @RequestParam(defaultValue = "20", name = "size") int pageSize,
            @RequestParam(defaultValue = "", name = "sortBy") String[] sortBy,
            @RequestParam(defaultValue = "", name = "ids") Long[] primaryKeys,
            @RequestParam(defaultValue = "", name = "days") Long days,
            @RequestParam(defaultValue = "", name = "author") Long author,
            @RequestParam(defaultValue = "0", name = "saved") Boolean savedOnly,
            @RequestParam(defaultValue = "0", name = "upVoted") Boolean upVotedOnly) {

        return pageService.searchAll(new String[] { "DISCUSSION" }, searchKey, primaryKeys,
                categories, tags, archived, published, exactSearch,
                pageNumber, pageSize, days, author, savedOnly, upVotedOnly, sortBy);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> getPostById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(postService.getPostById(id), "success"));
    }

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<PostDTO>> addPost(@RequestBody PostRequestDTO postDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(postService.addPost(postDTO), "Post successfully created!"));
    }

    @PostMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> editPost(@RequestBody PostDTO postDTO, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(postService.editPost(postDTO, id), "Post successfully edited!"));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> deletePost(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(postService.deletePost(id), "Post with id: " + id + "successfully deleted!"));
    }

    @PostMapping("/posts/{id}/active")
    public ResponseEntity<ApiResponse<PostDTO>> setPostArchive(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(postService.setActive(id), "success"));
    }

    @PatchMapping("/posts/{id}/allow-comment/true")
    public ResponseEntity<ApiResponse<PostDTO>> allowComment(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(postService.allowComment(id, true), "Comments are turned on in this post"));
    }

    @PatchMapping("/posts/{id}/allow-comment/false")
    public ResponseEntity<ApiResponse<PostDTO>> disallowComment(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(postService.allowComment(id, false), "Comments are turned off in this post"));
    }

}
