package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.entities.UserCommentRating;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.UserCommentRatingRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.UserCommentRatingService;
import org.springframework.stereotype.Service;

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
    public UserCommentRating rateComment (Long commentId, Long userId, String ratingValue){
        User user = userRepository.findById(userId).orElse(null);
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if(user == null || comment == null){
            return null;
        }

        UserCommentRating existingRating = userCommentRatingRepository.findByUserIdAndCommentId(userId,commentId);
        if(existingRating!=null){
            if (existingRating.getRating().equals(ratingValue)){
                existingRating.setVoted(false);
                existingRating.setRating("");
            }else{
                existingRating.setVoted(true);
                existingRating.setRating(ratingValue);
            }
            return userCommentRatingRepository.save(existingRating);
        }

        UserCommentRating newRating = new UserCommentRating(ratingValue,comment,user);
        return userCommentRatingRepository.save(newRating);
    }

    @Override
    public int totalCommentRating(Long commentId){
        int totalCommentRating = userCommentRatingRepository.totalCommentRating(commentId);
        Comment updatedComment = commentRepository.findById(commentId).orElseThrow();
        updatedComment.setTotalCommentRating(totalCommentRating);
        commentRepository.save(updatedComment);
        return totalCommentRating;
    }
}
