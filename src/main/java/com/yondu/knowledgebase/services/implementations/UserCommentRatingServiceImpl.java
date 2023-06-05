package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.UserCommentRatingDTO;
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
import java.util.stream.Collectors;

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
    public UserCommentRatingDTO rateComment (Long commentId, Long userId, String rating){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("User ID not found: %d", userId)));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException(String.format("Comment ID not found: %d", commentId)));
        checkRatingIfValid(rating);
        UserCommentRating existingRating = userCommentRatingRepository.findByUserIdAndCommentId(userId,commentId);
        if(existingRating!=null){
            existingRating = checkRatingValue (existingRating,rating);
            userCommentRatingRepository.save(existingRating);
        }else{
            UserCommentRating newRating = new UserCommentRating(rating,comment,user);
            userCommentRatingRepository.save(newRating);
        }
        int totalCommentRating = getTotalCommentRating(commentId);
        UserCommentRatingDTO userCommentRatingDTO = new UserCommentRatingDTO(rating,user.getId(),comment.getId(),totalCommentRating);
        return userCommentRatingDTO;
    }

    @Override
    public List<UserCommentRatingDTO> getAllCommentRating(){
/*        List <UserCommentRating> userCommentRatings = userCommentRatingRepository.findAll();
        List <UserCommentRatingDTO> userCommentRatingDTOS = userCommentRatings.stream().map(userCommentRating ->
            new UserCommentRatingDTO(userCommentRating.getRating(),userCommentRating.getUser().getId(),userCommentRating.getComment().getId(),userCommentRating.getComment().getTotalCommentRating()))
        .collect(Collectors.toList());
        return userCommentRatingDTOS;*/
        return null;
    }

    @Override
    public UserCommentRatingDTO getCommentRating (Long ratingId){
/*        UserCommentRating userCommentRating = userCommentRatingRepository.findById(ratingId).orElseThrow(() -> new ResourceNotFoundException(String.format("Rating ID not found: %d", ratingId)));
        UserCommentRatingDTO userCommentRatingDTO = new UserCommentRatingDTO(userCommentRating.getRating(),userCommentRating.getUser().getId(),userCommentRating.getComment().getId(),userCommentRating.getComment().getTotalCommentRating());
        return userCommentRatingDTO;*/
        return null;
    }

    @Override
    public UserCommentRatingDTO updateRating (String rating, Long ratingId){
        UserCommentRating userCommentRating = userCommentRatingRepository.findById(ratingId).orElseThrow(() -> new ResourceNotFoundException(String.format("Rating ID not found: %d", ratingId)));
        checkRatingIfValid(rating);
        userCommentRating = checkRatingValue (userCommentRating,rating);
        userCommentRatingRepository.save(userCommentRating);
        int totalCommentRating = getTotalCommentRating(userCommentRating.getComment().getId());
        UserCommentRatingDTO userCommentRatingDTO = new UserCommentRatingDTO(rating,userCommentRating.getUser().getId(),userCommentRating.getComment().getId(),totalCommentRating);
        return userCommentRatingDTO;
    }

    public int getTotalCommentRating(Long commentId){
/*        int totalCommentRating = userCommentRatingRepository.totalCommentRating(commentId);
        Comment updatedComment = commentRepository.findById(commentId).orElseThrow();
        updatedComment.setTotalCommentRating(totalCommentRating);
        commentRepository.save(updatedComment);
        return totalCommentRating;*/
        return 0;
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

    public void checkRatingIfValid(String rating){
        if(!(rating.equals("UP") || rating.equals("DOWN"))){
            throw new InvalidRatingException(String.format("Given rating is invalid"));
        }
    }
}
