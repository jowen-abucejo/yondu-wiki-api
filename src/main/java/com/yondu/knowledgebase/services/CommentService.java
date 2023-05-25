package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.entities.Comment;
import org.springframework.stereotype.Service;


public interface CommentService {
    public Comment createComment(Comment comment);
}
