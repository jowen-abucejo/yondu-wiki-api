package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.comment.CommentCountResponseDTO;
import com.yondu.knowledgebase.DTO.comment.CommentDTO;
import com.yondu.knowledgebase.DTO.comment.CommentRequestDTO;
import com.yondu.knowledgebase.DTO.comment.CommentResponseDTO;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.CommentService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private  final UserRepository userRepository;
    private final PageRepository pageRepository;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PageRepository pageRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.pageRepository = pageRepository;
    }

    @Override
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO, Long parentCommentId) {
        User user = userRepository.findById(commentRequestDTO.getUserId()).orElseThrow(()->new ResourceNotFoundException(String.format("User ID not found: %d", commentRequestDTO.getUserId())));
        if (commentRequestDTO.getEntityType().equals("PAGE")){
            pageRepository.findById(commentRequestDTO.getEntityId()).orElseThrow(() -> new ResourceNotFoundException(String.format("Page ID not found: %d", commentRequestDTO.getEntityId())));
        }else if (commentRequestDTO.getEntityType().equals("POST")){
            return null; //postRepository.findById(commentRequestDTO.getEntityId()).orElseThrow(() -> new ResourceNotFoundException(String.format("Post ID not found: %d", commentRequestDTO.getEntityId())));
        }

        LocalDateTime currentDate = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setDateCreated(currentDate);
        comment.setComment(commentRequestDTO.getComment());
        comment.setEntityId(commentRequestDTO.getEntityId());
        comment.setEntityType(commentRequestDTO.getEntityType());
        comment.setUser(user);

        if(parentCommentId != null){
            if(commentRepository.existsById(parentCommentId)){
                comment.setParentCommentId(parentCommentId);
            }else{
                throw new ResourceNotFoundException(String.format("Comment ID not found: %d",parentCommentId));
            }
        }

        commentRepository.save(comment);
        return mapToCommentResponseDTO(comment);
    }

    @Override
    public List<CommentResponseDTO> getAllComments(String entity, Long id) {
        List<Comment> comments = commentRepository.findByEntityTypeAndEntityId(entity,id);
        List <CommentResponseDTO> commentResponseList = new ArrayList<>();

        for (Comment comment : comments){
            CommentResponseDTO commentResponseDTO = mapToCommentResponseDTO(comment);
            commentResponseList.add(commentResponseDTO);
        }
        return commentResponseList;
    }

    @Override
    public CommentCountResponseDTO getTotalComments(String entity, Long id) {
        Long totalCommentCount = commentRepository.countByEntityTypeAndEntityId(entity,id);
        CommentCountResponseDTO commentCountResponseDTO = new CommentCountResponseDTO(id,entity,totalCommentCount);
        return commentCountResponseDTO;
    }

    @Override
    public CommentResponseDTO getComment (Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new ResourceNotFoundException(String.format("Comment ID not found: %d", commentId)));
        return mapToCommentResponseDTO(comment);
    }

    private CommentResponseDTO mapToCommentResponseDTO(Comment comment) {
        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
        commentResponseDTO.setId(comment.getId());
        commentResponseDTO.setDate(comment.getDateCreated());
        commentResponseDTO.setComment(comment.getComment());
        commentResponseDTO.setUserId(comment.getUser().getId());
        commentResponseDTO.setEntityId(comment.getEntityId());
        commentResponseDTO.setEntityType(comment.getEntityType());
        commentResponseDTO.setTotalReplies(commentRepository.countAllReplies(comment.getId()));

        //Fetch Replies
        List<Comment> comments = commentRepository.findAllCommentReplies(comment.getEntityType(),comment.getEntityId(),comment.getId());
        List <CommentDTO> commentReplies = comments.stream().map(reply -> new CommentDTO(reply.getId(),reply.getDateCreated(),reply.getComment(),reply.getUser().getId())).collect(Collectors.toList());
        commentResponseDTO.setReplies(commentReplies);

        return commentResponseDTO;
    }
}
