package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.post.PostDTO;
import com.yondu.knowledgebase.entities.Category;
import com.yondu.knowledgebase.entities.Post;
import com.yondu.knowledgebase.entities.Tag;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;
import com.yondu.knowledgebase.repositories.CategoryRepository;
import com.yondu.knowledgebase.repositories.PostRepository;
import com.yondu.knowledgebase.repositories.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    public List<PostDTO> getAllPost(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAll(pageable);

        return posts.getContent().stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());

    }

    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id +" not found!"));
        return new PostDTO(post);
    }

    public PostDTO addPost(PostDTO postDTO){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Set<Category> categories = postDTO.getCategories().stream().map(category -> {
            return categoryRepository.findById(category.getId()).get();
        }).collect(Collectors.toSet());

        Set<Tag> tags= postDTO.getTags().stream().map(tag -> {
            return tagRepository.findById(tag.getId()).get();
        }).collect(Collectors.toSet());

        Post post = new Post(postDTO.getId(), user, postDTO.getContent().replaceAll("<[^>]+>", ""), postDTO.getDateCreated(), postDTO.getDateModified(), true, false, categories, tags);
        postRepository.save(post);

        return new PostDTO(post);
    }


    public PostDTO editPost(PostDTO postDTO, Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Id with id: " + id + " not found!"));

        Set<Category> categories = postDTO.getCategories().stream().map(category -> {
            return categoryRepository.findById(category.getId()).get();
        }).collect(Collectors.toSet());

        Set<Tag> tags= postDTO.getTags().stream().map(tag -> {
            return tagRepository.findById(tag.getId()).get();
        }).collect(Collectors.toSet());

        post.setContent(postDTO.getContent().replaceAll("<[^>]+>", ""));
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

    public PostDTO setPostArchive(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post with id: " + id + " not found!"));

        post.setActive(false);

        postRepository.save(post);

        return new PostDTO(post);

    }
}
