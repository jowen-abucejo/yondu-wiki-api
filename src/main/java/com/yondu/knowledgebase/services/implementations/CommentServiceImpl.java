package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.Comment.CommentDTO;
import com.yondu.knowledgebase.DTO.Comment.CommentRequestDTO;
import com.yondu.knowledgebase.DTO.Comment.CommentResponseDTO;
import com.yondu.knowledgebase.DTO.Response;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.CommentService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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
    public Comment createComment(CommentDTO commentRequestDTO, Long commentParentId) {
        System.out.println("---------here: " + commentRequestDTO.getUserId());
        System.out.println("---------here: " + commentRequestDTO.getPageId());

        User user = userRepository.findById(commentRequestDTO.getUserId()).orElse(null);
        Page page = pageRepository.findByIdAndActive(commentRequestDTO.getPageId(), true).orElse(null);
        LocalDateTime currentDate = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setDateCreated(currentDate);
        comment.setComment(commentRequestDTO.getComment());
        comment.setTotalCommentRating(0);

        comment.setPage(page);

//        TO DO ---- Need PageRepository and  Rating
//        comment.setPage(commentReqDTO.getPageId());

        if (user == null) {
            throw new RuntimeException("User not found");
        }else{
            comment.setUser(user);
        }
        if(commentRequestDTO.getCommentParentId() != null){
            comment.setParentCommentId(commentRequestDTO.getCommentParentId());
        }
        return commentRepository.save(comment);
    }

    @Override
    public CommentResponseDTO getAllComments(Long pageId) {
        List<Comment> comments = commentRepository.findAllByParentCommentIdIsNullAndPageId(pageId);

        List<CommentRequestDTO> commentDTOs = comments.stream()
                .map(comment -> {
                    CommentRequestDTO commentDTO = new CommentRequestDTO(comment);
                    List<Comment> childComments = commentRepository.findAllByParentCommentIdAndPageId(comment.getId(), comment.getPage().getId());
                    List<CommentRequestDTO> childCommentDTOs = childComments.stream()
                            .map(CommentRequestDTO::new)
                            .collect(Collectors.toList());
                    commentDTO.setCommentParentList(childCommentDTOs);
                    return commentDTO;
                })
                .collect(Collectors.toList());

        CommentResponseDTO responseDTO = new CommentResponseDTO();
        responseDTO.setData(commentDTOs);
        responseDTO.setTotalComment(getTotalComments());
        responseDTO.setPageId(pageId);


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

    @Override
    public Comment getComment (Long commentId){
        Comment retieveComment = commentRepository.findById(commentId).orElseThrow(null);
        return retieveComment;
    }
}
