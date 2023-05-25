package com.yondu.knowledgebase.services.implimentations;

import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.services.CommentService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment createComment(Comment comment) {
        LocalDateTime currentDate = LocalDateTime.now();

        comment.setDateCreated(currentDate);
        return commentRepository.save(comment);
    }
}
