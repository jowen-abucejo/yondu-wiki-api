package com.yondu.knowledgebase.DTO.post;

import com.yondu.knowledgebase.DTO.category.CategoryDTO;
import com.yondu.knowledgebase.DTO.category.CategoryMapper;
import com.yondu.knowledgebase.DTO.tag.TagDTO;
import com.yondu.knowledgebase.DTO.tag.TagMapper;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Post;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.enums.ContentType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PostDTO {

    private Long id;
    private UserDTO.GeneralResponse author;
    private String content;
    private String title;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;
    private Boolean active = true;
    private Boolean deleted = false;
    private Boolean allowComment = true;
    private Set<CategoryDTO> categories = new HashSet<>();
    private Set<TagDTO> tags = new HashSet<>();
    private Set<UserDTO.GeneralResponse> postMentions = new HashSet<>();
    private String type;
    private Long commentCount;
    private Long upVoteCount;
    private Long totalRating;
    public PostDTO() {
    }

    public PostDTO(Post post, Long commentCount, Long upVoteCount) {
        this.id = post.getId();
        this.author = UserDTOMapper.mapToGeneralResponse(post.getAuthor());
        this.title = post.getTitle();
        this.type = ContentType.POST.getCode();
        this.content = post.getContent();
        this.dateCreated = post.getDateCreated();
        this.dateModified = post.getDateModified();
        this.active = post.getActive();
        this.deleted = post.getDeleted();
        this.allowComment = post.getAllowComment();
        this.tags = post.getTags().stream()
                .map(tag -> {
                    return new TagMapper().toDto(tag);
                })
                .collect(Collectors.toSet());
        this.categories = post.getCategories().stream()
                .map(category -> {
                    return new CategoryMapper().toDto(category);
                })
                .collect(Collectors.toSet());
        this.postMentions = post.getPostMentions().stream()
                .map(UserDTOMapper::mapToGeneralResponse)
                .collect(Collectors.toSet());
        this.commentCount = commentCount;
        this.upVoteCount = upVoteCount;
    }

    public PostDTO(Post post, Long commentCount, Long upVoteCount, Long totalRating) {
        this.id = post.getId();
        this.author = UserDTOMapper.mapToGeneralResponse(post.getAuthor());
        this.title = post.getTitle();
        this.type = ContentType.POST.getCode();
        this.content = post.getContent();
        this.dateCreated = post.getDateCreated();
        this.dateModified = post.getDateModified();
        this.active = post.getActive();
        this.deleted = post.getDeleted();
        this.allowComment = post.getAllowComment();
        this.tags = post.getTags().stream()
                .map(tag -> {
                    return new TagMapper().toDto(tag);
                })
                .collect(Collectors.toSet());
        this.categories = post.getCategories().stream()
                .map(category -> {
                    return new CategoryMapper().toDto(category);
                })
                .collect(Collectors.toSet());
        this.postMentions = post.getPostMentions().stream()
                .map(UserDTOMapper::mapToGeneralResponse)
                .collect(Collectors.toSet());
        this.commentCount = commentCount;
        this.upVoteCount = upVoteCount;
        this.totalRating = totalRating;
    }

    public Long getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(Long totalRating) {
        this.totalRating = totalRating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public UserDTO.GeneralResponse getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO.GeneralResponse author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(Boolean allowComment) {
        this.allowComment = allowComment;
    }

    public Set<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryDTO> categories) {
        this.categories = categories;
    }

    public Set<TagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    public Set<UserDTO.GeneralResponse> getPostMentions() {
        return postMentions;
    }

    public void setPostMentions(Set<UserDTO.GeneralResponse> postMentions) {
        this.postMentions = postMentions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getUpVoteCount() {
        return upVoteCount;
    }

    public void setUpVoteCount(Long upVoteCount) {
        this.upVoteCount = upVoteCount;
    }

    @Override
    public String toString() {
        return "PostDTO{" +
                "id=" + id +
                ", author=" + author +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", active=" + active +
                ", deleted=" + deleted +
                ", allowComment=" + allowComment +
                ", categories=" + categories +
                ", tags=" + tags +
                ", postMentions=" + postMentions +
                ", type='" + type + '\'' +
                ", commentCount=" + commentCount +
                ", upVoteCount=" + upVoteCount +
                ", totalRating=" + totalRating +
                '}';
    }
}
