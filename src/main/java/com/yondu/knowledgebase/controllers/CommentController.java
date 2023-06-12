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
        CommentDTO.BaseResponse comment = commentService.createComment(commentRequest, parentCommentId);
        String message = (parentCommentId==null) ? "Comment added successfully" : "Reply added successfully";
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(comment, message));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List <CommentDTO.BaseResponse>>> getAllComments(@RequestParam String entity, @RequestParam Long id) {
        List <CommentDTO.BaseResponse> comment= commentService.getAllComments(entity,id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(comment, "All comments retrieved successfully"));
    }

    @GetMapping ("/count")
    public ResponseEntity <ApiResponse<CommentDTO.CountResponse>> getTotalComments (@RequestParam String entity, @RequestParam Long id){
        CommentDTO.CountResponse response = commentService.getTotalComments(entity,id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Total Comment Count retrieved successfully"));

   }
    @GetMapping ("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO.BaseResponse>> getComment (@PathVariable Long commentId) {
        CommentDTO.BaseResponse response = commentService.getComment(commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Comment retrieved successfully"));
    }

    @PatchMapping ("/{commentId}/allow-reply")
    public ResponseEntity<ApiResponse<CommentDTO.BaseResponse>> allowReply (@PathVariable Long commentId, @RequestParam boolean status){
        CommentDTO.BaseResponse response = commentService.allowReply(commentId, status);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, String.format("Comment replies is turned %s", status)));
    }

}
