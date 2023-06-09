package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.ApiResponse;
import com.yondu.knowledgebase.DTO.comment.*;
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
    public ResponseEntity<ApiResponse<CommentDTO.BaseResponse>> createComment(@RequestBody CommentDTO.BaseRequest commentRequest, @RequestParam(required = false) Long parentCommentId) {
        CommentDTO.BaseResponse createdComment = commentService.createComment(commentRequest, parentCommentId);
        String message = (parentCommentId==null) ? "Comment added successfully" : "Reply added successfully";
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdComment, message));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List <CommentDTO.BaseResponse>>> getAllComments(@RequestParam String entity, @RequestParam Long id) {
        List <CommentDTO.BaseResponse> commentResponseDTO= commentService.getAllComments(entity,id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentResponseDTO, "All comments retrieved successfully"));
    }

    @GetMapping ("/count")
    public ResponseEntity <ApiResponse<CommentDTO.CountResponse>> getTotalComments (@RequestParam String entity, @RequestParam Long id){
        CommentDTO.CountResponse commentCountResponseDTO = commentService.getTotalComments(entity,id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentCountResponseDTO, "Total Comment Count retrieved successfully"));

   }
    @GetMapping ("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO.BaseResponse>> getComment (@PathVariable Long commentId) {
        CommentDTO.BaseResponse commentResponseDTO = commentService.getComment(commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentResponseDTO, "Comment retrieved successfully"));
    }

}
