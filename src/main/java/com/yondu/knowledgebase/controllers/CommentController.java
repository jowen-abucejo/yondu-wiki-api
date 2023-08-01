package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.comment.*;
import com.yondu.knowledgebase.DTO.comment.CommentDTO.BaseRatedResponse;
import com.yondu.knowledgebase.enums.ContentType;
import com.yondu.knowledgebase.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    //ADD a COMMENT / REPLY to a COMMENT in a PAGE
    @PostMapping ("/pages/{id}/comments")
    public ResponseEntity<ApiResponse<CommentDTO.BaseResponse>> addCommentInPage(@Valid @RequestBody CommentDTO.BaseRequest commentRequest, @PathVariable Long id, @RequestParam(required = false) Long parentCommentId) {
        CommentDTO.BaseResponse comment = commentService.createComment(commentRequest, parentCommentId, ContentType.PAGE.getCode(), id);
        String message = (parentCommentId == null) ? "Comment added successfully" : "Reply added successfully";
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(comment, message));
    }

    //ADD a COMMENT / REPLY to a COMMENT in a POST
    @PostMapping ("/posts/{id}/comments")
    public ResponseEntity<ApiResponse<CommentDTO.BaseResponse>> addCommentInPost(@Valid @RequestBody CommentDTO.BaseRequest commentRequest, @PathVariable Long id, @RequestParam(required = false) Long parentCommentId) {
        CommentDTO.BaseResponse comment = commentService.createComment(commentRequest, parentCommentId, ContentType.POST.getCode(), id);
        String message = (parentCommentId == null) ? "Comment added successfully" : "Reply added successfully";
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(comment, message));
    }

    //GET ALL comments in a PAGE
    @GetMapping ("/pages/{id}/comments")
    public ResponseEntity<ApiResponse<List<CommentDTO.BaseResponse>>> getAllCommentsInPage(@PathVariable Long id) {
        List<CommentDTO.BaseResponse> comment = commentService.getAllComments(ContentType.PAGE.getCode(), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(comment, "All comments retrieved successfully"));
    }

    //GET ALL comments in a POST
    @GetMapping ("/posts/{id}/comments")
    public ResponseEntity<ApiResponse<List<CommentDTO.BaseResponse>>> getAllCommentsInPost(@PathVariable Long id) {
        List<CommentDTO.BaseResponse> comment = commentService.getAllComments(ContentType.POST.getCode(), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(comment, "All comments retrieved successfully"));
    }

    //GET ALL PARENT comments in a PAGE
    @GetMapping ("/pages/{id}/comments/parent-comment")
    public ResponseEntity<ApiResponse<List<CommentDTO.ShortResponse>>> getAllParentCommentsInPage(@PathVariable Long id) {
        List<CommentDTO.ShortResponse> comment = commentService.getAllParentComments(ContentType.PAGE.getCode(), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(comment, "All parent comments retrieved successfully"));
    }

    //GET ALL PARENT comments in a POST
    @GetMapping ("/posts/{id}/comments/parent-comment")
    public ResponseEntity<ApiResponse<List<CommentDTO.ShortRatedResponse>>> getAllParentCommentsInPost(@PathVariable Long id) {
        List<CommentDTO.ShortRatedResponse> comment = commentService.getAllParentCommentsWithRate(ContentType.POST.getCode(), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(comment, "All parent comments retrieved successfully"));
    }

    //GET TOTAL comment COUNT in a PAGE
    @GetMapping("/pages/{id}/comments/count")
    public ResponseEntity<ApiResponse<CommentDTO.CountResponse>> getTotalCommentsInPage(@PathVariable Long id) {
        CommentDTO.CountResponse response = commentService.getTotalComments(ContentType.PAGE.getCode(), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Total Comment Count retrieved successfully"));

    }

    //GET TOTAL comment COUNT in a POST
    @GetMapping("/posts/{id}/comments/count")
    public ResponseEntity<ApiResponse<CommentDTO.CountResponse>> getTotalCommentsInPost(@PathVariable Long id) {
        CommentDTO.CountResponse response = commentService.getTotalComments(ContentType.POST.getCode(), id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Total Comment Count retrieved successfully"));

    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO.BaseResponse>> getComment(@PathVariable Long commentId) {
        CommentDTO.BaseResponse response = commentService.getComment(commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Comment retrieved successfully"));
    }

    @PatchMapping("/comments/{commentId}/allow-reply/true")
    public ResponseEntity<ApiResponse<CommentDTO.ShortResponse>> allowReply(@PathVariable Long commentId) {
        CommentDTO.ShortResponse response = commentService.allowReply(commentId, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, String.format("Replies are turned on in this comment with id %d", commentId)));
    }

    @PatchMapping("/comments/{commentId}/allow-reply/false")
    public ResponseEntity<ApiResponse<CommentDTO.ShortResponse>> disallowReply(@PathVariable Long commentId) {
        CommentDTO.ShortResponse response = commentService.allowReply(commentId, false);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, String.format("Replies are turned off in this comment with id %d", commentId)));
    }

    @DeleteMapping("/comments/{commentId}/delete/true")
    public ResponseEntity<ApiResponse<CommentDTO.ShortResponse>> deleteComment(@PathVariable Long commentId) {
        CommentDTO.ShortResponse response = commentService.deleteComment(commentId, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, String.format("Comment with id %d is deleted", commentId)));
    }

    @DeleteMapping("/comments/{commentId}/delete/false")
    public ResponseEntity<ApiResponse<CommentDTO.ShortResponse>> undeleteComment(@PathVariable Long commentId) {
        CommentDTO.ShortResponse response = commentService.deleteComment(commentId, false);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, String.format("Comment with id %d is undeleted", commentId)));
    }

    @GetMapping("/comments/{commentId}/reply")
    public ResponseEntity<ApiResponse<List<CommentDTO.ShortResponse>>> getReplies(@PathVariable Long commentId) {
        List<CommentDTO.ShortResponse> response = commentService.getReplies(commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, String.format("All replies retrieved for comment with ID %d", commentId)));
    }

    @GetMapping("/comments/search")
    public ResponseEntity<ApiResponse<List<CommentDTO.ShortResponse>>> searchComments(@RequestParam String key) {
        List<CommentDTO.ShortResponse> response = commentService.searchComments(key);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Successfully searched comments"));
    }

}