package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.post.PostDTO;
import com.yondu.knowledgebase.DTO.post.PostRequestDTO;
import com.yondu.knowledgebase.DTO.rating.RatingDTO;
import com.yondu.knowledgebase.DTO.rating.TotalVoteDTO;
import com.yondu.knowledgebase.entities.Category;
import com.yondu.knowledgebase.entities.Post;
import com.yondu.knowledgebase.entities.Tag;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.ContentType;
import com.yondu.knowledgebase.enums.EntityType;
import com.yondu.knowledgebase.enums.NotificationType;
import com.yondu.knowledgebase.enums.PageType;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CategoryRepository;
import com.yondu.knowledgebase.repositories.PostRepository;
import com.yondu.knowledgebase.repositories.TagRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final RatingService ratingService;
    
    @Value("${fe.frontend-link}")
    private String FRONTEND_LINK;

    @Autowired
    private ChatbaseService chatbaseService;

    public PostService(PostRepository postRepository, CategoryRepository categoryRepository,
            TagRepository tagRepository, UserRepository userRepository, NotificationService notificationService,
            AuditLogService auditLogService, RatingService ratingService) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
        this.ratingService = ratingService;
    }

    public PostDTO getPostById(Long id) {
        List<Object[]> results = postRepository.findPostWithCommentAndUpvoteCountsById(id);

        if (results.isEmpty()) {
            return null;
        }

        Object[] result = results.get(0);
        Post post = (Post) result[0];
        Long commentCount = (Long) result[1];
        Long upVoteCount = (Long) result[2];
        return new PostDTO(post, commentCount, upVoteCount, voteType(post.getId()), totalVote(post.getId()));
    }

    public PostDTO addPost(PostRequestDTO postDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Set<Category> categories = postDTO.getCategories().stream().map(category -> {
            return categoryRepository.findById(category.getId()).get();
        }).collect(Collectors.toSet());

        Set<Tag> tags = postDTO.getTags().stream().map(tag -> {
            if (tagRepository.existsByName(tag.getName())) {
                return tagRepository.findByName(tag.getName()).get();
            } else {
                Tag newTag = new Tag();
                newTag.setName(tag.getName());
                return newTag;
            }
        }).collect(Collectors.toSet());

        Set<User> mentions = getMentionedUsers(postDTO.getPostMentions());
        Post post = new Post(postDTO.getId(), user, postDTO.getTitle(), postDTO.getContent(), LocalDateTime.now(),
                postDTO.getDateModified(), true, false, true, categories, tags, mentions);
        post.setModifiedContent(postDTO.getContent().replaceAll("<[^>]+>", ""));
        postRepository.save(post);

        auditLogService.createAuditLog(user, EntityType.POST.getCode(), post.getId(), "created a post");

        // Notify mentioned Users in a POST
        for (User mentionedUser : mentions) {
            // Notify all mentioned users in the created comment
            String fromUser = post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName();
            notificationService.createNotification(new NotificationDTO.BaseRequest(mentionedUser.getId(),
                    post.getAuthor().getId(), String.format("%s mentioned you in their post", fromUser),
                    NotificationType.MENTION.getCode(), ContentType.POST.getCode(), post.getId()),                     
                    getLinksForEmailNotificationTemplate(PageType.DISCUSSION.getCode(), post.getId()));
        }
        chatbaseService.updateChatbot(post);
        return new PostDTO(post, 0L, 0L, null, 0);
    }

    public PostDTO editPost(PostRequestDTO postDTO, Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));

        Set<Category> categories = postDTO.getCategories().stream().map(category -> {
            return categoryRepository.findById(category.getId()).get();
        }).collect(Collectors.toSet());

        Set<Tag> tags = postDTO.getTags().stream().map(tag -> {
            if (tagRepository.existsByName(tag.getName())) {
                return tagRepository.findByName(tag.getName()).get();
            } else {
                Tag newTag = new Tag();
                newTag.setName(tag.getName());
                return newTag;
            }
        }).collect(Collectors.toSet());

        Set<User> mentions = getMentionedUsers(postDTO.getPostMentions());

        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setModifiedContent(postDTO.getContent().replaceAll("<[^>]+>", ""));
        post.setCategories(categories);
        post.setTags(tags);
        post.setDateModified(LocalDateTime.now());
        post.setPostMentions(mentions);

        postRepository.save(post);

        auditLogService.createAuditLog(user, EntityType.POST.getCode(), post.getId(), "edited a post");

        return new PostDTO(post, null, null, null, 0);
    }

    public PostDTO deletePost(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));

        post.setDeleted(true);

        postRepository.save(post);

        auditLogService.createAuditLog(user, EntityType.POST.getCode(), post.getId(), "deleted a post");

        return new PostDTO(post, null, null, null, 0);
    }

    public PostDTO setActive(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));

        post.setActive(!post.getActive());

        postRepository.save(post);

        auditLogService.createAuditLog(user, EntityType.POST.getCode(), post.getId(), "archived a post");

        return new PostDTO(post, null, null, null, 0);
    }

    public PostDTO allowComment(Long id, boolean allowComment) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));

        post.setAllowComment(allowComment);

        postRepository.save(post);

        return new PostDTO(post, null, null, null, 0);
    }

    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));
    }

    private Set<User> getMentionedUsers(Long[] userIds) {
        Set<User> mentionedUsers = new HashSet<>();
        for (Long userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("User ID not found: %d", userId)));
            if (user != null) {
                mentionedUsers.add(user);
            }
        }
        return mentionedUsers;
    }

    private String voteType(Long postId) {
        RatingDTO rating = ratingService.ratingByEntityIdAndEntityType(postId, "Post");
        return rating.getRating();
    }

    private Integer totalVote(Long postId) {
        TotalVoteDTO vote = ratingService.totalVote(postId, "Post");
        return vote.getTotal_vote();
    }

    	private Map<String, String> getLinksForEmailNotificationTemplate(String contentType, Long entityId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, String> data = new HashMap<>();
        data.put("fromUserLink", String.format("%s/profile?author=%s", FRONTEND_LINK, user.getEmail()));
        data.put("contentLink", String.format("%s/posts/%ss/%d", FRONTEND_LINK, contentType.toLowerCase(), entityId));
		data.put("contentType", contentType.toLowerCase());
        return data;
    }
}
