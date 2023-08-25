package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "author", referencedColumnName = "id", updatable = false, nullable = false)
    private User author;

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime dateCreated;

    @LastModifiedDate
    private LocalDateTime dateModified;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active = true;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = false;

    @Column(name = "allow_comment", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean allowComment = true;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "post_category", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "post_tag", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "post_mentions", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> postMentions = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String modifiedContent;

    public Post() {
    }

    public Post(Long id, User author, String title, String content, LocalDateTime dateCreated,
            LocalDateTime dateModified, Boolean active, Boolean deleted, Boolean allowComment, Set<Category> categories,
            Set<Tag> tags, Set<User> postMentions) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.active = active;
        this.deleted = deleted;
        this.allowComment = allowComment;
        this.categories = categories;
        this.tags = tags;
        this.postMentions = postMentions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
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

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<User> getPostMentions() {
        return postMentions;
    }

    public void setPostMentions(Set<User> postMentions) {
        this.postMentions = postMentions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModifiedContent() {
        return modifiedContent;
    }

    public void setModifiedContent(String modifiedContent) {
        this.modifiedContent = modifiedContent;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", author=" + author +
                ", title='" + title + '\'' +
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
