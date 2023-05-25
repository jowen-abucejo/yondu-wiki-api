package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.CommentReqDTO;
import com.yondu.knowledgebase.entities.Comment;
import org.springframework.stereotype.Service;


public interface CommentService {
    public Comment createComment(CommentReqDTO comment);
}
