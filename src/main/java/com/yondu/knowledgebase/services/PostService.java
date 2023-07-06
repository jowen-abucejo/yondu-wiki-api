package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;
import com.yondu.knowledgebase.DTO.post.PostDTO;
import com.yondu.knowledgebase.DTO.post.PostRequestDTO;
import com.yondu.knowledgebase.DTO.post.PostSearchResult;
import com.yondu.knowledgebase.Utils.MultipleSort;
import com.yondu.knowledgebase.Utils.NativeQueryUtils;
import com.yondu.knowledgebase.entities.Category;
import com.yondu.knowledgebase.entities.Post;
import com.yondu.knowledgebase.entities.Tag;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.ContentType;
import com.yondu.knowledgebase.enums.EntityType;
import com.yondu.knowledgebase.enums.NotificationType;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CategoryRepository;
import com.yondu.knowledgebase.repositories.PostRepository;
import com.yondu.knowledgebase.repositories.TagRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
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

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    public PostService(PostRepository postRepository, CategoryRepository categoryRepository,
            TagRepository tagRepository, UserRepository userRepository, NotificationService notificationService,
            AuditLogService auditLogService) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    public PaginatedResponse<PostDTO> getAllPost(int page, int size, String searchKey) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Object[]> postResults = postRepository.searchPostsWithCommentAndUpvoteCounts(searchKey, pageable);

        List<PostDTO> postDTOs = postResults.getContent().stream()
                .map(result -> {
                    Post post = (Post) result[0];
                    Long commentCount = (Long) result[1];
                    Long upVoteCount = (Long) result[2];
                    return new PostDTO(post, commentCount, upVoteCount);
                })
                .collect(Collectors.toList());

        return new PaginatedResponse<>(postDTOs, page, size, (long) postResults.getTotalElements());
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
        return new PostDTO(post, commentCount, upVoteCount);
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
                    NotificationType.MENTION.getCode(), ContentType.POST.getCode(), post.getId()));
        }
        return new PostDTO(post, 0L, 0L);
    }

    public PostDTO editPost(PostDTO postDTO, Long id) {
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

        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setModifiedContent(postDTO.getContent().replaceAll("<[^>]+>", ""));
        post.setCategories(categories);
        post.setTags(tags);
        post.setDateModified(LocalDateTime.now());

        postRepository.save(post);

        auditLogService.createAuditLog(user, EntityType.POST.getCode(), post.getId(), "edited a post");

        return new PostDTO(post, null, null);
    }

    public PostDTO deletePost(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));

        post.setDeleted(true);

        postRepository.save(post);

        auditLogService.createAuditLog(user, EntityType.POST.getCode(), post.getId(), "deleted a post");

        return new PostDTO(post, null, null);
    }

    public PostDTO setActive(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));

        post.setActive(!post.getActive());

        postRepository.save(post);

        auditLogService.createAuditLog(user, EntityType.POST.getCode(), post.getId(), "archived a post");

        return new PostDTO(post, null, null);

    }

    public PostDTO allowComment(Long id, boolean allowComment) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));

        post.setAllowComment(allowComment);

        postRepository.save(post);

        return new PostDTO(post, null, null);

    }

    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));
    }

    private Set<User> getMentionedUsers(Long[] userIds) {
        Set<User> mentionedUsers = new HashSet<>();
        for (Long userId : userIds) {
            System.out.println(userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("User ID not found: %d", userId)));
            if (user != null) {
                mentionedUsers.add(user);
            }
        }
        return mentionedUsers;
    }

    public List<PostDTO> findTop5MostPopularPosts(int page, int size, Integer days) {
        LocalDateTime startDate = (days != null) ? LocalDateTime.now().minusDays(days) : null;
        PageRequest pageable = PageRequest.of(page - 1, size);
        List<Object[]> results;

        if (startDate != null) {
            results = postRepository.findMostPopularPosts(startDate, pageable);
        } else {
            results = postRepository.findMostPopularPosts(null, pageable);
        }

        return results.stream()
                .map(result -> {
                    Post post = (Post) result[0];
                    Long commentCount = (Long) result[1];
                    Long upVoteCount = (Long) result[2];
                    return new PostDTO(post, commentCount, upVoteCount);
                })
                .collect(Collectors.toList());
    }

    public List<PostDTO> findTopPosts(int page, int size) {
        PageRequest pageable = PageRequest.of(page - 1, size);
        List<Object[]> results = postRepository.findTopPosts(pageable);

        return results.stream()
                .map(result -> {
                    Post post = (Post) result[0];
                    Long commentCount = (Long) result[1];
                    Long upVoteCount = (Long) result[2];
                    return new PostDTO(post, commentCount, upVoteCount);
                })
                .collect(Collectors.toList());
    }

    public PaginatedResponse<PostDTO> searchPostsByUser(int page, int size, String searchKey, Boolean active,
            Boolean deleted) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Object[]> postResults = postRepository.searchPostsWithCommentAndUpvoteCounts(searchKey, active, deleted,
                user.getId(), pageable);

        List<PostDTO> postDTOs = postResults.getContent().stream()
                .map(result -> {
                    Post post = (Post) result[0];
                    Long commentCount = (Long) result[1];
                    Long upVoteCount = (Long) result[2];
                    return new PostDTO(post, commentCount, upVoteCount);
                })
                .collect(Collectors.toList());

        return new PaginatedResponse<>(postDTOs, page, size, (long) postResults.getTotalElements());
    }

    public PaginatedResponse<PostSearchResult> findAllByFullTextSearch(
            String searchKey, String[] categories, String[] tags,
            Boolean isArchive, Boolean exactSearch, Integer pageNumber,
            Integer pageSize, String[] sortBy) {
        int retrievedPage = Math.max(1, pageNumber);

        // configure pageable size and orders
        var validSortAliases = Arrays.asList("dateModified", "dateCreated", "relevance", "totalComments",
                "totalRatings");
        var nativeSort = MultipleSort.sortWithOrders(sortBy, new String[] { "dateModified,desc" },
                new HashSet<>(validSortAliases));
        Pageable paging = PageRequest.of(retrievedPage - 1, pageSize, Sort.by(nativeSort));
        paging = MultipleSort.sortByAliases(paging);

        // format search key words
        searchKey = searchKey.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        /***********************
         * TODO: Refactor to simplify implementations
         ********************************/
        // Fetch ids and relevance scores, will use to display sorted and filtered posts
        var postResults = postRepository.findByFullTextSearch(
                searchKey, exactSearch, isArchive, NativeQueryUtils.arrayToSqlStringList(categories),
                NativeQueryUtils.arrayToSqlStringList(tags), paging)
                .orElse(null);

        // create a list of ids of posts to fetch
        List<Long> postResults2 = postResults.getContent().stream().map(result -> (Long) result.get("postId"))
                .collect(Collectors.toList());

        // fetch all post entities
        var postEntitiesList = postRepository.findAllById(postResults2);

        // create a post map required to achieved a O(1) complexity when mapping the
        // results
        Map<Long, Post> postEntitiesMap = new HashMap<>();
        postEntitiesList.forEach(post -> {
            postEntitiesMap.put(post.getId(), post);
        });

        // return a sorted post dtos with additional field of relevance
        var postList = postResults.getContent().stream().map(result -> {
            var post = postEntitiesMap.get((Long) result.get("postId"));
            return new PostSearchResult(
                    post,
                    BigDecimal.valueOf((Double) result.get("relevance")),
                    (Long) result.get("totalComments"),
                    (Long) result.get("totalRatings"));
        }).collect(Collectors.toList());

        /***********************
         * TODO: END
         ********************************/

        var otherConfiguration = new HashMap<String, Object>();
        otherConfiguration.put("available_sorting", validSortAliases);
        otherConfiguration.put("applied_sorting", postResults.getSort()
                .map(order -> String.format("%s,%s", order.getProperty(), order.getDirection()))
                .toList());

        return new PaginatedResponse<>(postList, retrievedPage, pageSize, postResults.getTotalElements(),
                otherConfiguration);
    }
}
