package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.Comment.CommentRequestDTO;
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
    public ResponseEntity<?> createComment(@RequestBody CommentRequestDTO commentRequest, @RequestParam Long userId, @RequestParam(required = false) Long commentParentId) {
        Comment createdComment = commentService.createComment(commentRequest, userId, commentParentId);

        if (createdComment != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(commentRequest);
        }
    }

    @GetMapping
    public CommentResponseDTO getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping ("/{commentId}")
    public Comment getComment (@PathVariable Long commentId) {
        return commentService.getComment(commentId);
    }
}
