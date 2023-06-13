package com.yondu.knowledgebase.controllers;


import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.post.PostDTO;
import com.yondu.knowledgebase.DTO.post.PostRequestDTO;
import com.yondu.knowledgebase.services.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<PostDTO>>> getAllPost(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(postService.getAllPost(page, size),"success"));
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> getPostById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(postService.getPostById(id), "success"));
    }

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<PostDTO>> addPost(@RequestBody PostRequestDTO postDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(postService.addPost(postDTO), "Post successfully created!"));
    }

    @PostMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> editPost(@RequestBody PostDTO postDTO, @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(postService.editPost(postDTO, id), "Post successfully edited!"));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> deletePost(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(postService.deletePost(id),"Post with id: " + id + "successfully deleted!"));
    }

    @PostMapping("/posts/{id}/active")
    public ResponseEntity<ApiResponse<PostDTO>> setPostArchive(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(postService.setActive(id), "success"));
    }

    @PostMapping("/posts/{id}/allow-comment")
    public ResponseEntity<ApiResponse<PostDTO>> allowComment(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(postService.allowComment(id), "success"));
    }
}
