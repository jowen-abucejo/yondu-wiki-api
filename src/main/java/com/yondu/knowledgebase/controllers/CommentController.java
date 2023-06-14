package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.comment.*;
import com.yondu.knowledgebase.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentDTO.BaseResponse>> createComment(@Valid @RequestBody CommentDTO.BaseRequest commentRequest, @RequestParam(required = false) Long parentCommentId) {
        CommentDTO.BaseResponse comment = commentService.createComment(commentRequest, parentCommentId);
        String message = (parentCommentId == null) ? "Comment added successfully" : "Reply added successfully";
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(comment, message));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentDTO.BaseResponse>>> getAllComments(@RequestParam String entity, @RequestParam Long id) {
        List<CommentDTO.BaseResponse> comment = commentService.getAllComments(entity, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(comment, "All parent comments retrieved successfully"));
    }

    @GetMapping ("/parent-comment")
    public ResponseEntity<ApiResponse<List<CommentDTO.BaseComment>>> getAllParentComments(@RequestParam String entity, @RequestParam Long id) {
        List<CommentDTO.BaseComment> comment = commentService.getAllParentComments(entity, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(comment, "All parent comments retrieved successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<CommentDTO.CountResponse>> getTotalComments(@RequestParam String entity, @RequestParam Long id) {
        CommentDTO.CountResponse response = commentService.getTotalComments(entity, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Total Comment Count retrieved successfully"));

    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO.BaseResponse>> getComment(@PathVariable Long commentId) {
        CommentDTO.BaseResponse response = commentService.getComment(commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Comment retrieved successfully"));
    }

    @PatchMapping("/{commentId}/allow-reply")
    public ResponseEntity<ApiResponse<CommentDTO.BaseComment>> allowReply(@PathVariable Long commentId) {
        CommentDTO.BaseComment response = commentService.allowReply(commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, String.format("allowReply is successfully updated", commentId)));
    }

    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<ApiResponse<CommentDTO.BaseComment>> deleteComment(@PathVariable Long commentId) {
        CommentDTO.BaseComment response = commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, String.format("isDeleted successfully updated", commentId)));
    }

    @GetMapping("/{commentId}/reply")
    public ResponseEntity<ApiResponse<List<CommentDTO.BaseComment>>> getReplies(@PathVariable Long commentId) {
        List<CommentDTO.BaseComment> response = commentService.getReplies(commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, String.format("All replies retrieved for comment with ID %d", commentId)));
    }

}