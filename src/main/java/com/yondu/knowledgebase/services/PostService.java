package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.DTO.post.PostDTO;
import com.yondu.knowledgebase.DTO.post.PostRequestDTO;
import com.yondu.knowledgebase.entities.Category;
import com.yondu.knowledgebase.entities.Post;
import com.yondu.knowledgebase.entities.Tag;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.ContentType;
import com.yondu.knowledgebase.enums.NotificationType;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CategoryRepository;
import com.yondu.knowledgebase.repositories.PostRepository;
import com.yondu.knowledgebase.repositories.TagRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PostService(PostRepository postRepository, CategoryRepository categoryRepository, TagRepository tagRepository, UserRepository userRepository, NotificationService notificationService) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<PostDTO> getAllPost(int page, int size, String searchKey){
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.searchPosts(searchKey, pageable);

        return posts.getContent().stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());

    }

    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id +" not found!"));
        return new PostDTO(post);
    }

    public PostDTO addPost(PostRequestDTO postDTO){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Set<Category> categories = postDTO.getCategories().stream().map(category -> {
            return categoryRepository.findById(category.getId()).get();
        }).collect(Collectors.toSet());

        Set<Tag> tags= postDTO.getTags().stream().map(tag -> {
            if(tagRepository.existsByName(tag.getName())){
                return tagRepository.findByName(tag.getName()).get();
            }
            else{
                Tag newTag = new Tag();
                newTag.setName(tag.getName());
                return newTag;
            }
        }).collect(Collectors.toSet());

        Set<User> mentions = getMentionedUsers(postDTO.getPostMentions());
        Post post = new Post(postDTO.getId(), user, postDTO.getTitle(), postDTO.getContent(), LocalDateTime.now(), postDTO.getDateModified(), true, false, true, categories, tags, mentions);
        postRepository.save(post);

        //Notify mentioned Users in a POST
        for (User mentionedUser:mentions){
            //Notify all mentioned users in the created comment
            String fromUser = post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName();
            notificationService.createNotification(new NotificationDTO.BaseRequest(mentionedUser.getId(),post.getAuthor().getId(),String.format("%s mentioned you in their post", fromUser), NotificationType.MENTION.getCode(), ContentType.POST.getCode(), post.getId()));
        }
        return new PostDTO(post);
    }


    public PostDTO editPost(PostDTO postDTO, Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));

        Set<Category> categories = postDTO.getCategories().stream().map(category -> {
            return categoryRepository.findById(category.getId()).get();
        }).collect(Collectors.toSet());

        Set<Tag> tags= postDTO.getTags().stream().map(tag -> {
            return tagRepository.findById(tag.getId()).get();
        }).collect(Collectors.toSet());

        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCategories(categories);
        post.setTags(tags);
        post.setDateModified(LocalDateTime.now());

        postRepository.save(post);

        return new PostDTO(post);
    }

    public PostDTO deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));

        post.setDeleted(true);

        postRepository.save(post);

        return new PostDTO(post);
    }

    public PostDTO setActive(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));

        post.setActive(!post.getActive());

        postRepository.save(post);

        return new PostDTO(post);

    }

    public PostDTO allowComment(Long id, boolean allowComment) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));

        post.setAllowComment(allowComment);

        postRepository.save(post);

        return new PostDTO(post);

    }

    public Post getPost(Long id){
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));
    }

    private Set<User> getMentionedUsers(Long[] userIds) {
        Set<User> mentionedUsers = new HashSet<>();
        for (Long userId : userIds) {
            System.out.println(userId);
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("User ID not found: %d", userId)));
            if (user != null) {
                mentionedUsers.add(user);
            }
        }
        return mentionedUsers;
    }
}
