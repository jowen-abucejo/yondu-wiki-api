package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.comment.*;
import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.enums.ContentType;
import com.yondu.knowledgebase.enums.NotificationType;
import com.yondu.knowledgebase.exceptions.CommentIsNotAllowed;
import com.yondu.knowledgebase.exceptions.InvalidNotificationTypeException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.PostRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.AuditLogService;
import com.yondu.knowledgebase.services.CommentService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PageRepository pageRepository;
    private final NotificationServiceImpl notificationService;
    private final PageServiceImpl pageService;
    private final AuditLogService auditLogService;


    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository, PageRepository pageRepository, NotificationServiceImpl notificationService, PageServiceImpl pageService, AuditLogService auditLogService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.pageRepository = pageRepository;
        this.notificationService = notificationService;
        this.pageService = pageService;
        this.auditLogService = auditLogService;
    }

    @Override
    public CommentDTO.BaseResponse createComment(CommentDTO.BaseRequest request, Long parentCommentId, String entityType, Long entityId) {
        Map<String,Object> data = new HashMap<>();
        //Get current User
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (entityType.equals(ContentType.PAGE.getCode())){
            PageDTO page = pageService.findById(entityId);
            if (page!=null && !page.getAllowComment())
                throw new CommentIsNotAllowed("Comments are turned off in this page");
            else{
                Page selectedPage = pageRepository.findById(page.getId()).orElseThrow(()->new ResourceNotFoundException(String.format("Page ID not found: %d", page.getId())));
                data.put("contentType","PAGE");
                data.put("authorId",selectedPage.getAuthor().getId());
                data.put("contentId",page.getId());
            }
        }else if (entityType.equals(ContentType.POST.getCode())){
            Post post = postRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("Post ID not found: %d", entityId)));
            if (post.getActive() && post.getAllowComment()){
                data.put("contentType","POST");
                data.put("authorId",post.getAuthor().getId());
                data.put("contentId",post.getId());
            }else {
                throw new CommentIsNotAllowed("Comments are turned off in this post");
            }
        }else throw new InvalidNotificationTypeException("Invalid entity type");

        Comment comment = CommentDTOMapper.mapToComment(request, user, entityType, entityId);
        String fromUser = user.getFirstName() + " " + user.getLastName();
        String contentType = data.get("contentType").toString();
        Long toUserId = (Long)data.get("authorId");
        Long contentId = (Long)data.get("contentId");

        //If comment is a reply
        if(parentCommentId != null){
            if(commentRepository.existsById(parentCommentId)){
                comment.setParentCommentId(parentCommentId);
                //Notify the User of the Parent Comment being replied on - Pass the ID and the Entity ID of the User and its created comment
                Comment parentComment = commentRepository.findById(parentCommentId).orElseThrow(()->new ResourceNotFoundException(String.format("Comment ID not found : %d",parentCommentId)));
                if(!parentComment.isAllowReply())
                    throw new CommentIsNotAllowed("Replies are turned off in this comment");
                User parentCommentUser = userRepository.findById(parentComment.getUser().getId()).orElseThrow(()->new ResourceNotFoundException(String.format("User ID not found : %d",parentComment.getUser().getId())));
                notificationService.createNotification(new NotificationDTO.BaseRequest(parentCommentUser.getId(),user.getId(),String.format("%s replied on your comment", fromUser), NotificationType.COMMENT.getCode(), ContentType.REPLY.getCode(), parentCommentId));
            }else{
                throw new ResourceNotFoundException(String.format("Comment ID not found: %d",parentCommentId));
            }
        }else {
            //User to be notified that there is a new comment in the content
            notificationService.createNotification(new NotificationDTO.BaseRequest(toUserId,user.getId(),String.format("%s added a comment on your %s", fromUser, contentType.toLowerCase()), NotificationType.COMMENT.getCode(), contentType, contentId));
        }
        commentRepository.save(comment);

        for (User mentionedUser:comment.getCommentMentions()){
            //Notify all mentioned users in the created comment
            notificationService.createNotification(new NotificationDTO.BaseRequest(mentionedUser.getId(),user.getId(),String.format("%s mentioned you in a comment", fromUser), NotificationType.MENTION.getCode(), contentType, comment.getId()));
        }

        if(parentCommentId!=null){
            Comment parentComment = commentRepository.findById(parentCommentId).orElseThrow(()->new ResourceNotFoundException(String.format("Comment ID not found : %d",parentCommentId)));
            Set<Comment> commentReplies = parentComment.getCommentReplies();
            commentReplies.add(comment);
            parentComment.setCommentReplies(commentReplies);
            commentRepository.save(parentComment);
            auditLogService.createAuditLog(user,ContentType.REPLY.getCode(), parentComment.getId(), String.format("replied to a comment id: %d", parentCommentId));
        }else
            auditLogService.createAuditLog(user,contentType, entityId, String.format("created a comment id: %d to a %s with the ID %d", comment.getId(), contentType, entityId));
        return CommentDTOMapper.mapToBaseResponse(comment);
    }

    @Override
    public List<CommentDTO.BaseResponse> getAllComments(String entity, Long id) {
        List<Comment> comments = commentRepository.getAllComments(entity,id);
        List <CommentDTO.BaseResponse> commentResponseList = new ArrayList<>();
        for (Comment comment : comments){
            CommentDTO.BaseResponse baseResponse = CommentDTOMapper.mapToBaseResponse(comment);
            commentResponseList.add(baseResponse);
        }
        return commentResponseList;
    }

    @Override
    public List<CommentDTO.ShortResponse> getAllParentComments (String entity, Long id){
        List<Comment> comments = commentRepository.getAllParentComments(entity,id);
        List <CommentDTO.ShortResponse> commentResponseList = new ArrayList<>();
        for (Comment comment: comments){
            CommentDTO.ShortResponse shortResponse = CommentDTOMapper.mapToShortResponse(comment);
            commentResponseList.add(shortResponse);
        }
        return commentResponseList;
    }

    @Override
    public CommentDTO.CountResponse getTotalComments(String entityType,Long entityId) {
        Long totalCommentCount = commentRepository.countByEntityTypeAndEntityId(entityType, entityId);
        return CommentDTOMapper.mapToCountResponse(entityType,entityId,totalCommentCount);
    }

    @Override
    public CommentDTO.BaseResponse getComment (Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new ResourceNotFoundException(String.format("Comment ID not found: %d", commentId)));
        return CommentDTOMapper.mapToBaseResponse(comment);
    }

    @Override
    public CommentDTO.ShortResponse allowReply (Long id, boolean allowReply){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = commentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(String.format("Comment ID not found: %d", id)));
        String message = allowReply ? "on" : "off";
        comment.setAllowReply(allowReply);
        commentRepository.save(comment);
        auditLogService.createAuditLog(user,ContentType.REPLY.getCode(), comment.getId(), String.format("turned %s replies in this comment id:%d", message,comment.getId()));
        return CommentDTOMapper.mapToShortResponse(comment);
    }

    @Override
    public CommentDTO.ShortResponse deleteComment(Long id, boolean delete){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = commentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(String.format("Comment ID not found: %d", id)));
        String message = delete ? "deleted" : "undeleted";
        comment.setDeleted(delete);
        commentRepository.save(comment);
        auditLogService.createAuditLog(user,ContentType.REPLY.getCode(), comment.getId(), String.format("%s this comment id:%d", message,comment.getId()));
        return CommentDTOMapper.mapToShortResponse(comment);
    }

    @Override
    public List <CommentDTO.ShortResponse> getReplies (Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(String.format("Comment ID not found: %d", id)));
        List<Comment> comments = commentRepository.findAllCommentReplies(comment.getEntityType(), comment.getEntityId(), comment.getId());
        if (comments != null)
            return comments.stream().map(CommentDTOMapper::mapToShortResponse).collect(Collectors.toList());
        return null;
    }

    @Override
    public List <CommentDTO.ShortResponse> searchComments (String key){
        List<Comment> comments = commentRepository.searchComments(key);
        return comments.stream().map(CommentDTOMapper::mapToShortResponse).collect(Collectors.toList());
    }
}
