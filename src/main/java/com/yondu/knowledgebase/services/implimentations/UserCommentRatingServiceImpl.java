package com.yondu.knowledgebase.services.implimentations;

import com.yondu.knowledgebase.DTO.UserCommentRatingDTO;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.entities.UserCommentRating;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.UserCommentRatingRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.UserCommentRatingService;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserCommentRatingServiceImpl implements UserCommentRatingService {
    private final UserCommentRatingRepository userCommentRatingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public UserCommentRatingServiceImpl (UserCommentRatingRepository userCommentRatingRepository, UserRepository userRepository, CommentRepository commentRepository){
        this.userCommentRatingRepository = userCommentRatingRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public UserCommentRating addCommentRating (UserCommentRatingDTO userCommentRatingReqDTO){
        User user = userRepository.findById(userCommentRatingReqDTO.getUserId()).orElse(null);
        Comment comment = commentRepository.findById(userCommentRatingReqDTO.getCommentId()).orElse(null);
        UserCommentRating userCommentRating = new UserCommentRating();
        userCommentRating.setRating(userCommentRatingReqDTO.getRating());

        if(user != null && comment != null){
            userCommentRating.setUser(user);
            userCommentRating.setComment(comment);
            return userCommentRatingRepository.save(userCommentRating);
        }else {
            return null;
        }

    }

}
