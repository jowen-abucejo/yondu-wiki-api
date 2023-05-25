package com.yondu.knowledgebase.services.implimentations;

import com.yondu.knowledgebase.DTO.CommentReqDTO;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.CommentService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private  final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Comment createComment(CommentReqDTO commentReqDTO) {
        User user = userRepository.findById(commentReqDTO.getUserId()).orElse(null);
        LocalDateTime currentDate = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setDateCreated(commentReqDTO.getDate());
        comment.setComment(commentReqDTO.getComment());

//        TO DO ---- Need PageRepository and  Rating
//        comment.setPage(commentReqDTO.getPageId());
//        comment.setRatingId(commentReqDTO.getRatingId());


        if (user == null) {
            throw new RuntimeException("User not found");
        }else{
            comment.setUser(user);
        }
        return commentRepository.save(comment);
    }
}
