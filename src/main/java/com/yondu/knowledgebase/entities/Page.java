package com.yondu.knowledgebase.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime dateCreated;

    @CreatedBy
    private User author;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "is_deleted")
    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name = "directory_id", referencedColumnName = "id")
    private Directory directory;

    @OneToMany(mappedBy = "page")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "page")
    private List<PageLock> pageLock;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "page_category", joinColumns = @JoinColumn(name = "page_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "page_tag", joinColumns = @JoinColumn(name = "page_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    /**
     * 
     */
    public Page() {
    }

    /**
     * @param id
     * @param dateCreated
     * @param author
     * @param active
     * @param deleted
     * @param directory
     * @param comments
     * @param pageLock
     * @param categories
     * @param tags
     */
    public Page(Long id, LocalDateTime dateCreated, User author, Boolean active, Boolean deleted, Directory directory,
            List<Comment> comments, List<PageLock> pageLock, List<Category> categories, List<Tag> tags) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.author = author;
        this.active = active;
        this.deleted = deleted;
        this.directory = directory;
        this.comments = comments;
        this.pageLock = pageLock;
        this.categories = categories;
        this.tags = tags;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the dateCreated
     */
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    /**
     * @return the author
     */
    public User getAuthor() {
        return author;
    }

    /**
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @return the deleted
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * @return the directory
     */
    public Directory getDirectory() {
        return directory;
    }

    /**
     * @return the comments
     */
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * @return the pageLock
     */
    public List<PageLock> getPageLock() {
        return pageLock;
    }

    /**
     * @return the categories
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * @return the tags
     */
    public List<Tag> getTags() {
        return tags;
    }

}
