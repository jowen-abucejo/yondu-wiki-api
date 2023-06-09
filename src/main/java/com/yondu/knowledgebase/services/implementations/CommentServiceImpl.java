package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.comment.*;
import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.entities.Comment;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.Post;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.ContentType;
import com.yondu.knowledgebase.enums.NotificationType;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.PostRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.CommentService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private  final UserRepository userRepository;
    private final PageRepository pageRepository;
    private final PostRepository postRepository;
    private final NotificationServiceImpl notificationService;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PageRepository pageRepository, PostRepository postRepository, NotificationServiceImpl notificationService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.pageRepository = pageRepository;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
    }

    @Override
    public CommentDTO.BaseResponse createComment(CommentDTO.BaseRequest request, Long parentCommentId) {
        Map<String,Object> data = new HashMap<>();

        User user = userRepository.findById(request.userId()).orElseThrow(()->new ResourceNotFoundException(String.format("User ID not found: %d", request.userId())));
        if (request.entityType().equals(ContentType.PAGE.getCode())){
            Page page = pageRepository.findById(request.entityId()).orElseThrow(() -> new ResourceNotFoundException(String.format("Page ID not found: %d", request.entityId())));
            data.put("contentType",ContentType.PAGE.getCode());
            data.put("authorId",page.getAuthor().getId());
            data.put("contentId",page.getId());
        }else if (request.entityType().equals(ContentType.POST.getCode())){
            Post post = postRepository.findById(request.entityId()).orElseThrow(() -> new ResourceNotFoundException(String.format("Post ID not found: %d", request.entityId())));
            data.put("contentType",ContentType.POST.getCode());
            data.put("authorId",post.getAuthor().getId());
            data.put("contentId",post.getId());
        }

        Comment comment = CommentDTOMapper.mapToComment(request, user);

        //If comment is a reply
        if(parentCommentId != null){
            if(commentRepository.existsById(parentCommentId)){
                comment.setParentCommentId(parentCommentId);
                //Notify the User of the Parent Comment being replied on - Pass the ID and the Entity ID of the User and its created comment
                Comment parentComment = commentRepository.findById(parentCommentId).orElseThrow(()->new ResourceNotFoundException(String.format("Comment ID not found : %d",parentCommentId)));
                User parentCommentUser = userRepository.findById(parentComment.getUser().getId()).orElseThrow(()->new ResourceNotFoundException(String.format("User ID not found : %d",parentComment.getUser().getId())));
                notificationService.createNotification(new NotificationDTO.BaseRequest(parentCommentUser.getId(),"Someone replied on your comment!", NotificationType.COMMENT.getCode(), ContentType.COMMENT.getCode(),parentCommentId));
            }else{
                throw new ResourceNotFoundException(String.format("Comment ID not found: %d",parentCommentId));
            }
        }else {
            //User to be notified that there is a new comment in the content
            notificationService.createNotification(new NotificationDTO.BaseRequest((Long)data.get("authorId"),"Someone commented on your content!", NotificationType.COMMENT.getCode(), data.get("contentType").toString(), (Long)data.get("contentId")));
        }

        commentRepository.save(comment);
        return CommentDTOMapper.mapToBaseResponse(comment);
    }

    @Override
    public List<CommentDTO.BaseResponse> getAllComments(String entity, Long id) {
        List<Comment> comments = commentRepository.findByEntityTypeAndEntityId(entity,id);
        List <CommentDTO.BaseResponse> commentResponseList = new ArrayList<>();
        for (Comment comment : comments){
            CommentDTO.BaseResponse baseResponse = CommentDTOMapper.mapToBaseResponse(comment);
            commentResponseList.add(baseResponse);
        }
        return commentResponseList;
    }

    @Override
    public CommentDTO.CountResponse getTotalComments(String entityType,Long entityId) {
        return CommentDTOMapper.mapToCountResponse(entityType,entityId);
    }

    @Override
    public CommentDTO.BaseResponse getComment (Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new ResourceNotFoundException(String.format("Comment ID not found: %d", commentId)));
        return CommentDTOMapper.mapToBaseResponse(comment);
    }
}
