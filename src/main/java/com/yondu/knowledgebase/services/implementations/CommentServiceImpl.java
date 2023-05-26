package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.Comment.CommentRequestDTO;
import com.yondu.knowledgebase.DTO.Comment.CommentResponseDTO;
import com.yondu.knowledgebase.DTO.Response;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.CommentService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Comment createComment(CommentRequestDTO commentRequestDTO, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        LocalDateTime currentDate = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setDateCreated(currentDate);
        comment.setComment(commentRequestDTO.getComment());

        // TO DO ---- Need PageRepository and Rating
        // comment.setPage(commentReqDTO.getPageId());
        // comment.setRatingId(commentReqDTO.getRatingId());

        if (user == null) {
            throw new RuntimeException("User not found");
        } else {
            comment.setUser(user);
        }
        return commentRepository.save(comment);
    }

    @Override
    public CommentResponseDTO getAllComments() {
        List<Comment> comments = commentRepository.findAll();

        List<CommentRequestDTO> commentDTOs = new ArrayList<>();
        for (Comment comment : comments) {
            CommentRequestDTO commentDTO = new CommentRequestDTO(comment);
            commentDTOs.add(commentDTO);
        }

        CommentResponseDTO responseDTO = new CommentResponseDTO();
        responseDTO.setData(commentDTOs);
        responseDTO.setTotalComment(getTotalComments());

        // Temp Response message
        Response response = new Response();
        response.setCode(200);
        response.setMessage("Success");

        responseDTO.setResponse(response);

        return responseDTO;
    }

    @Override
    public int getTotalComments() {
        System.out.println(Math.toIntExact(commentRepository.count()));
        return Math.toIntExact(commentRepository.count());
    }
}
