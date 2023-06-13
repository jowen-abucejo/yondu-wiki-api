package com.yondu.knowledgebase.DTO.post;

import com.yondu.knowledgebase.DTO.category.CategoryDTO;
import com.yondu.knowledgebase.DTO.category.CategoryMapper;
import com.yondu.knowledgebase.DTO.tag.TagDTO;
import com.yondu.knowledgebase.DTO.tag.TagMapper;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Post;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.exceptions.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PostRequestDTO {

    private Long id;
    private UserDTO.BaseResponse author;
    private String content;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;
    private Boolean active = true;
    private Boolean deleted = false;
    private Boolean allowComment = true;
    private Set<CategoryDTO> categories = new HashSet<>();
    private Set<TagDTO> tags = new HashSet<>();
    private Long [] postMentions;
    public PostRequestDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO.BaseResponse getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO.BaseResponse author) {
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

    public Long[] getPostMentions() {
        return postMentions;
    }

    public void setPostMentions(Long[] postMentions) {
        this.postMentions = postMentions;
    }

    @Override
    public String toString() {
        return "PostDTO{" +
                "id=" + id +
                ", author=" + author +
                ", content='" + content + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", active=" + active +
                ", deleted=" + deleted +
                ", allowComment=" + allowComment +
                ", categories=" + categories +
                ", tags=" + tags +
                ", postMentions=" + postMentions +
                '}';
    }
}
