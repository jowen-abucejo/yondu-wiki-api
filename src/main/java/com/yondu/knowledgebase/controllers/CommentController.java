package com.yondu.knowledgebase.controllers;

import com.yondu.knowledgebase.DTO.CommentReqDTO;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.services.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final  CommentService commentService;

    public CommentController(CommentService commentService ) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentReqDTO commentRequest) {
        Comment comment = new Comment();
        comment.setDateCreated(commentRequest.getDate());
        comment.setComment(commentRequest.getComment());
//        To do -- Set Page ID and User ID
        Comment createdComment = commentService.createComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }
}
