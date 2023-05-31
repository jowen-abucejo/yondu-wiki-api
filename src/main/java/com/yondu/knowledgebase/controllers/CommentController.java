package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.Comment.CommentDTO;
import com.yondu.knowledgebase.DTO.Comment.CommentResponseDTO;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.services.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final  CommentService commentService;

    public CommentController(CommentService commentService ) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createComment(@RequestBody CommentDTO commentRequest, @RequestParam(required = false) Long commentParentId) {
        CommentDTO createdComment = commentService.createComment(commentRequest, commentParentId);
        String message = (commentParentId==null) ? "Comment added successfully" : "Reply added successfully";
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdComment, message));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllComments( @RequestParam Long pageId) {
        CommentResponseDTO commentResponseDTO= commentService.getAllComments(pageId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentResponseDTO, "All comments retrieved successfully"));
    }

    @GetMapping ("/{commentId}")
    public ResponseEntity<ApiResponse<?>> getComment (@PathVariable Long commentId) {
        Comment comment = commentService.getComment(commentId);
        CommentDTO commentResponseDTO = new CommentDTO(comment.getComment(),comment.getPage().getId(),comment.getUser().getId(),comment.getParentCommentId(),comment.getTotalCommentRating());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentResponseDTO, "Comment retrieved successfully"));
    }
}
