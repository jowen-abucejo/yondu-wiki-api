package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.entities.UserCommentRating;
import com.yondu.knowledgebase.exceptions.InvalidRatingException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.UserCommentRatingRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.UserCommentRatingService;
import org.springframework.stereotype.Service;

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
    public UserCommentRating rateComment (Long commentId, Long userId, String rating){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("User ID not found: %d", userId)));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException(String.format("Comment ID not found: %d", commentId)));
        checkRatingIfValid(rating);
        UserCommentRating existingRating = userCommentRatingRepository.findByUserIdAndCommentId(userId,commentId);
        if(existingRating!=null){
            checkRatingValue (existingRating,rating);
            return userCommentRatingRepository.save(existingRating);
        }
        UserCommentRating newRating = new UserCommentRating(rating,comment,user);
        return userCommentRatingRepository.save(newRating);
    }

    @Override
    public int getTotalCommentRating(Long commentId){
        int totalCommentRating = userCommentRatingRepository.totalCommentRating(commentId);
        Comment updatedComment = commentRepository.findById(commentId).orElseThrow();
        updatedComment.setTotalCommentRating(totalCommentRating);
        commentRepository.save(updatedComment);
        return totalCommentRating;
    }

    @Override
    public List<UserCommentRating> getAllCommentRating(){
        return userCommentRatingRepository.findAll();
    }

    @Override
    public UserCommentRating getCommentRating (Long ratingId){
        return userCommentRatingRepository.findById(ratingId).orElseThrow(() -> new ResourceNotFoundException(String.format("Rating ID not found: %d", ratingId)));
    }

    @Override
    public UserCommentRating updateRating (String rating, Long ratingId){
        UserCommentRating userCommentRating = userCommentRatingRepository.findById(ratingId).orElseThrow(() -> new ResourceNotFoundException(String.format("Rating ID not found: %d", ratingId)));
        checkRatingIfValid(rating);
        checkRatingValue (userCommentRating,rating);
        return userCommentRatingRepository.save(userCommentRating);
    }

    public UserCommentRating checkRatingValue (UserCommentRating userCommentRating, String rating){
        if (userCommentRating.getRating().equals(rating)){
            userCommentRating.setVoted(false);
            userCommentRating.setRating("");
        }else{
            userCommentRating.setVoted(true);
            userCommentRating.setRating(rating);
        }
        return userCommentRating;
    }

    public String checkRatingIfValid(String rating){
        if(!(rating.equals("UP") || rating.equals("DOWN"))){
            throw new InvalidRatingException(String.format("Given rating is invalid"));
        }
        return rating;
    }
}
