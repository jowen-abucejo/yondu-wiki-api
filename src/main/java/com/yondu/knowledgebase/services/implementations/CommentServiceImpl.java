package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.comment.*;
import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.rating.RatingDTO;
import com.yondu.knowledgebase.DTO.rating.TotalUpvoteDTO;
import com.yondu.knowledgebase.DTO.rating.TotalVoteDTO;
import com.yondu.knowledgebase.entities.*;
import com.yondu.knowledgebase.enums.ContentType;
import com.yondu.knowledgebase.enums.NotificationType;
import com.yondu.knowledgebase.enums.PageType;
import com.yondu.knowledgebase.exceptions.CommentIsNotAllowed;
import com.yondu.knowledgebase.exceptions.InvalidNotificationTypeException;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CommentRepository;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.repositories.PostRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import com.yondu.knowledgebase.services.AuditLogService;
import com.yondu.knowledgebase.services.CommentService;
import com.yondu.knowledgebase.services.RatingService;

import org.springframework.beans.factory.annotation.Value;
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
    private final RatingService ratingService;

    @Value("${fe.frontend-link}")
    private String FRONTEND_LINK;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository,
            PostRepository postRepository, PageRepository pageRepository, NotificationServiceImpl notificationService,
            PageServiceImpl pageService, AuditLogService auditLogService, RatingService ratingService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.pageRepository = pageRepository;
        this.notificationService = notificationService;
        this.pageService = pageService;
        this.auditLogService = auditLogService;
        this.ratingService = ratingService;
    }

    @Override
    public CommentDTO.BaseResponse createComment(CommentDTO.BaseRequest request, Long parentCommentId,
            String entityType, Long entityId) {
        Map<String, Object> data = new HashMap<>();
        // Get current User
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (entityType.equals(ContentType.PAGE.getCode())) {
            PageDTO page = pageService.findById(entityId);
            if (page != null && !page.getAllowComment())
                throw new CommentIsNotAllowed("Comments are turned off in this page");
            else {
                Page selectedPage = pageRepository.findById(page.getId()).orElseThrow(
                        () -> new ResourceNotFoundException(String.format("Page ID not found: %d", page.getId())));
                if (selectedPage.getType().equals(ContentType.WIKI.getCode())) {
                    data.put("contentType", ContentType.WIKI.getCode());
                } else if (selectedPage.getType().equals(ContentType.ANNOUNCEMENT.getCode())) {
                    data.put("contentType", ContentType.ANNOUNCEMENT.getCode());
                }
                data.put("contentTitle", page.getBody().getTitle());
                data.put("authorId", selectedPage.getAuthor().getId());
                data.put("contentId", page.getId());
            }
        } else if (entityType.equals(ContentType.POST.getCode())) {
            Post post = postRepository.findById(entityId)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Post ID not found: %d", entityId)));
            if (post.getActive() && post.getAllowComment()) {
                data.put("contentTitle", post.getTitle());
                data.put("contentType", ContentType.POST.getCode());
                data.put("authorId", post.getAuthor().getId());
                data.put("contentId", post.getId());
            } else {
                throw new CommentIsNotAllowed("Comments are turned off in this post");
            }
        } else
            throw new InvalidNotificationTypeException("Invalid entity type");

        Comment comment = CommentDTOMapper.mapToComment(request, user, entityType, entityId);
        Set<User> commentMentions = Arrays.stream(request.commentMentions())
                .map(id -> userRepository.findById(id).get()).collect(Collectors.toSet());
        comment.setCommentMentions(commentMentions);
        String fromUser = user.getFirstName() + " " + user.getLastName();
        String contentType = data.get("contentType").toString();
        Long toUserId = (Long) data.get("authorId");
        Long contentId = (Long) data.get("contentId");

        // If comment is a reply
        if (parentCommentId != null) {
            if (commentRepository.existsById(parentCommentId)) {
                comment.setParentCommentId(parentCommentId);
                // Notify the User of the Parent Comment being replied on - Pass the ID and the
                // Entity ID of the User and its created comment
                Comment parentComment = commentRepository.findById(parentCommentId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                String.format("Comment ID not found : %d", parentCommentId)));
                if (!parentComment.isAllowReply())
                    throw new CommentIsNotAllowed("Replies are turned off in this comment");
                User parentCommentUser = userRepository.findById(parentComment.getUser().getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                String.format("User ID not found : %d", parentComment.getUser().getId())));
                notificationService
                        .createNotification(new NotificationDTO.BaseRequest(parentCommentUser.getId(), user.getId(),
                                String.format("%s replied \"%s\" on your comment ", fromUser,
                                        formatStringForNotification(comment.getComment())),
                                NotificationType.COMMENT.getCode(), ContentType.REPLY.getCode(), parentCommentId), getLinksForEmailNotificationTemplate(parentComment.getEntityType(), parentComment.getEntityId()));
            } else {
                throw new ResourceNotFoundException(String.format("Comment ID not found: %d", parentCommentId));
            }
        } else {
            // User to be notified that there is a new comment in the content
            notificationService.createNotification(new NotificationDTO.BaseRequest(toUserId, user.getId(),
                    String.format("%s added a comment \"%s\" on your %s \"%s\"", fromUser,
                            formatStringForNotification(comment.getComment()), contentType.toLowerCase(),
                            data.get("contentTitle".toString())),
                    NotificationType.COMMENT.getCode(), contentType, contentId), getLinksForEmailNotificationTemplate(contentType, contentId));
        }
        commentRepository.save(comment);

        for (User mentionedUser : comment.getCommentMentions()) {
            // Notify all mentioned users in the created comment
            notificationService.createNotification(new NotificationDTO.BaseRequest(mentionedUser.getId(), user.getId(),
                    String.format("%s mentioned you in a comment", fromUser), NotificationType.MENTION.getCode(),
                    contentType, comment.getId()), getLinksForEmailNotificationTemplate(contentType, contentId));
        }

        if (parentCommentId != null) {
            Comment parentComment = commentRepository.findById(parentCommentId).orElseThrow(
                    () -> new ResourceNotFoundException(String.format("Comment ID not found : %d", parentCommentId)));
            Set<Comment> commentReplies = parentComment.getCommentReplies();
            commentReplies.add(comment);
            parentComment.setCommentReplies(commentReplies);
            commentRepository.save(parentComment);
            auditLogService.createAuditLog(user, ContentType.REPLY.getCode(), parentComment.getId(),
                    String.format("replied \"%s\" to a comment", formatStringForNotification(comment.getComment())));
        }

        auditLogService.createAuditLog(user, ContentType.REPLY.getCode(), comment.getId(),
                String.format("created a comment \"%s\" in the content type of %s titled \"%s\"",
                        formatStringForNotification(comment.getComment()), contentType.toLowerCase(),
                        data.get("contentTitle".toString())));
        return CommentDTOMapper.mapToBaseResponse(comment);
    }

    @Override
    public List<CommentDTO.BaseResponse> getAllComments(String entity, Long id) {
        List<Comment> comments = commentRepository.getAllComments(entity, id);
        List<CommentDTO.BaseResponse> commentResponseList = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDTO.BaseResponse baseResponse = CommentDTOMapper.mapToBaseResponse(comment);
            commentResponseList.add(baseResponse);
        }
        return commentResponseList;
    }

    @Override
    public List<CommentDTO.ShortRatedResponse> getAllParentCommentsWithRate(String entity, Long id) {
        List<Comment> comments = commentRepository.getAllParentComments(entity, id);
        List<CommentDTO.ShortRatedResponse> commentResponseList = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDTO.ShortRatedResponse shortResponse = CommentDTOMapper.mapToShortRatedResponse(comment,
                    voteType(comment.getId()), upvoteCount(comment.getId()), totalVote(comment.getId()));
            commentResponseList.add(shortResponse);
        }
        return commentResponseList;
    }

    @Override
    public List<CommentDTO.ShortResponse> getAllParentComments(String entity, Long id) {
        List<Comment> comments = commentRepository.getAllParentComments(entity, id);
        List<CommentDTO.ShortResponse> commentResponseList = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDTO.ShortResponse shortResponse = CommentDTOMapper.mapToShortResponse(comment);
            commentResponseList.add(shortResponse);
        }
        return commentResponseList;
    }

    @Override
    public CommentDTO.CountResponse getTotalComments(String entityType, Long entityId) {
        Long totalCommentCount = commentRepository.countByEntityTypeAndEntityId(entityType, entityId);
        return CommentDTOMapper.mapToCountResponse(entityType, entityId, totalCommentCount);
    }

    @Override
    public CommentDTO.BaseResponse getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Comment ID not found: %d", commentId)));
        return CommentDTOMapper.mapToBaseResponse(comment);
    }

    @Override
    public CommentDTO.ShortResponse allowReply(Long id, boolean allowReply) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Comment ID not found: %d", id)));
        String message = allowReply ? "on" : "off";
        comment.setAllowReply(allowReply);
        commentRepository.save(comment);
        auditLogService.createAuditLog(user, ContentType.REPLY.getCode(), comment.getId(),
                String.format("turned %s replies", message, comment.getId()));
        return CommentDTOMapper.mapToShortResponse(comment);
    }

    @Override
    public CommentDTO.ShortResponse deleteComment(Long id, boolean delete) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Comment ID not found: %d", id)));
        String message = delete ? "deleted" : "undeleted";
        comment.setDeleted(delete);
        commentRepository.save(comment);
        auditLogService.createAuditLog(user, ContentType.REPLY.getCode(), comment.getId(),
                String.format("%s a comment", message, comment.getId()));
        return CommentDTOMapper.mapToShortResponse(comment);
    }

    @Override
    public List<CommentDTO.ShortResponse> getReplies(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Comment ID not found: %d", id)));
        List<Comment> comments = commentRepository.findAllCommentReplies(comment.getEntityType(), comment.getEntityId(),
                comment.getId());
        if (comments != null)
            return comments.stream().map(CommentDTOMapper::mapToShortResponse).collect(Collectors.toList());
        return null;
    }

    @Override
    public List<CommentDTO.ShortResponse> searchComments(String key) {
        List<Comment> comments = commentRepository.searchComments(key);
        return comments.stream().map(CommentDTOMapper::mapToShortResponse).collect(Collectors.toList());
    }

    public String formatStringForNotification(String text) {
        String noHtmlTags = text.replaceAll("<[^>]+>", "");
        String noSpecialEntities = noHtmlTags.replaceAll("&nbsp;", " ")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&")
                .replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'");
        String cleanText = noSpecialEntities.replaceAll("\\s+", " ").trim();
        if (cleanText.length() > 30) {
            return cleanText.substring(0, 30) + "...";
        }
        return cleanText;
    }

    private String voteType(Long id) {
        RatingDTO rating = ratingService.ratingByEntityIdAndEntityType(id, "Comment");
        return rating.getRating();
    }

    private Integer upvoteCount(Long id) {
        /* upvote - downvote */
        TotalUpvoteDTO vote = ratingService.totalUpvote(id, "Comment");
        return vote.getTotal_upvote();
    }

    private Integer totalVote(Long id) {
        TotalVoteDTO vote = ratingService.totalVote(id, "Comment");
        /* upvote + downvote */
        return vote.getTotal_vote();
    }

	private Map<String, String> getLinksForEmailNotificationTemplate(String contentType, Long entityId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, String> data = new HashMap<>();
        data.put("fromUserLink", String.format("%s/profile?author=%s", FRONTEND_LINK, user.getEmail()));

        if (contentType.toUpperCase().equals(ContentType.POST.getCode())) {
            data.put("contentLink", String.format("%s/posts/%ss/%d", FRONTEND_LINK, PageType.DISCUSSION.getCode().toLowerCase(), entityId));
		    data.put("contentType", PageType.DISCUSSION.getCode().toLowerCase());
        } else if (contentType.toUpperCase().equals(ContentType.ANNOUNCEMENT.getCode())
                || contentType.toUpperCase().equals(ContentType.WIKI.getCode())) {
            data.put("contentLink", String.format("%s/posts/%ss/%d", FRONTEND_LINK, contentType.toLowerCase(), entityId));
		    data.put("contentType", contentType.toLowerCase());
        } else {
            com.yondu.knowledgebase.entities.Page page = pageRepository.findById(entityId).orElse(null);
            data.put("contentLink", String.format("%s/posts/%ss/%d", FRONTEND_LINK, page.getType().toLowerCase(), entityId));
		    data.put("contentType", page.getType().toLowerCase());
        }
        return data;
    }

}
