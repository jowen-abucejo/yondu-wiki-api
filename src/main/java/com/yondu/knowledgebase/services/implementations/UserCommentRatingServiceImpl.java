package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.UserCommentRatingDTO;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.entities.UserCommentRating;
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

    public UserCommentRatingServiceImpl(UserCommentRatingRepository userCommentRatingRepository,
            UserRepository userRepository, CommentRepository commentRepository) {
        this.userCommentRatingRepository = userCommentRatingRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public UserCommentRating rateComment(Long commentId, Long userId, String ratingValue) {
        User user = userRepository.findById(userId).orElse(null);
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (user == null || comment == null) {
            return null;
        }

        UserCommentRating existingRating = userCommentRatingRepository.findByUserIdAndCommentId(userId, commentId);
        if (existingRating != null) {
            existingRating.setRating(ratingValue);
            return userCommentRatingRepository.save(existingRating);
        }

        UserCommentRating newRating = new UserCommentRating(ratingValue, comment, user);
        return userCommentRatingRepository.save(newRating);
    }

    @Override
    public int totalCommentRating(Long commentId) {
        List<UserCommentRating> userCommentRatings = userCommentRatingRepository.findByCommentId(commentId);
        int totalCommentRating = 0;
        for (UserCommentRating ratings : userCommentRatings) {
            if (ratings.getRating().equals("UP")) {
                totalCommentRating += 1;
            }
        }

        return totalCommentRating;
    }
}
