package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.comment.CommentCountResponseDTO;
import com.yondu.knowledgebase.DTO.comment.CommentRequestDTO;
import com.yondu.knowledgebase.DTO.comment.CommentResponseDTO;
import com.yondu.knowledgebase.services.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final  CommentService commentService;

    public CommentController(CommentService commentService ) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponseDTO>> createComment(@RequestBody CommentRequestDTO commentRequest, @RequestParam(required = false) Long parentCommentId) {
        CommentResponseDTO createdComment = commentService.createComment(commentRequest, parentCommentId);
        String message = (parentCommentId==null) ? "Comment added successfully" : "Reply added successfully";
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdComment, message));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List <CommentResponseDTO>>> getAllComments(@RequestParam String entity, @RequestParam Long id) {
        List <CommentResponseDTO> commentResponseDTO= commentService.getAllComments(entity,id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentResponseDTO, "All comments retrieved successfully"));
    }

    @GetMapping ("/count")
    public ResponseEntity <ApiResponse<CommentCountResponseDTO>> getTotalCommentCount (@RequestParam String entity, @RequestParam Long id){
        CommentCountResponseDTO commentCountResponseDTO = commentService.getTotalComments(entity,id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentCountResponseDTO, "Total Comment Count retrieved successfully"));

    }
    @GetMapping ("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDTO>> getComment (@PathVariable Long commentId) {
        CommentResponseDTO commentResponseDTO = commentService.getComment(commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentResponseDTO, "Comment retrieved successfully"));
    }
}
