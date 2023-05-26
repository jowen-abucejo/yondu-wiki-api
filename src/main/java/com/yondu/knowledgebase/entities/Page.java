package com.yondu.knowledgebase.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime dateCreated;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "author", referencedColumnName = "id")
    private User author;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "is_deleted")
    private Boolean deleted;

    @Column(name = "lock_start", nullable = true)
    private LocalDateTime lockStart;

    @Column(name = "lock_end", nullable = true)
    private LocalDateTime lockEnd;

    @Column(name = "locked_by", nullable = true)
    private User lockedBy;

    @ManyToOne(fetch = FetchType.LAZY)
   // @JsonBackReference
    @JoinColumn(name = "directory_id", referencedColumnName = "id")
    private Directory directory;

    @OneToMany(mappedBy = "page")
  //  @JsonBackReference
    @OrderBy(value = "modified_by DESC")
    private List<PageVersion> pageVersions = new ArrayList<>();

    @OneToMany(mappedBy = "page")
   // @JsonBackReference
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
   // @JsonBackReference
    @JoinTable(name = "page_category", joinColumns = @JoinColumn(name = "page_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
   // @JsonBackReference
    @JoinTable(name = "page_tag", joinColumns = @JoinColumn(name = "page_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "page")
   // @JsonBackReference
    private Set<UserPagePermission> userPagePermissions = new HashSet<>();

    @OneToMany(mappedBy = "page")
  //  @JsonBackReference
    private Set<UserPageRating> userPageRatings = new HashSet<>();

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
     * @param lockStart
     * @param lockEnd
     * @param lockedBy
     * @param directory
     * @param pageVersions
     * @param comments
     * @param categories
     * @param tags
     * @param userPagePermissions
     * @param userPageRatings
     */
    public Page(Long id, LocalDateTime dateCreated, User author, Boolean active, Boolean deleted,
            LocalDateTime lockStart, LocalDateTime lockEnd, User lockedBy, Directory directory,
            List<PageVersion> pageVersions, List<Comment> comments, Set<Category> categories, Set<Tag> tags,
            Set<UserPagePermission> userPagePermissions, Set<UserPageRating> userPageRatings) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.author = author;
        this.active = active;
        this.deleted = deleted;
        this.lockStart = lockStart;
        this.lockEnd = lockEnd;
        this.lockedBy = lockedBy;
        this.directory = directory;
        this.pageVersions = pageVersions;
        this.comments = comments;
        this.categories = categories;
        this.tags = tags;
        this.userPagePermissions = userPagePermissions;
        this.userPageRatings = userPageRatings;
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
     * @return the lockStart
     */
    public LocalDateTime getLockStart() {
        return lockStart;
    }

    /**
     * @return the lockEnd
     */
    public LocalDateTime getLockEnd() {
        return lockEnd;
    }

    /**
     * @return the lockedBy
     */
    public User getLockedBy() {
        return lockedBy;
    }

    /**
     * @return the directory
     */
    public Directory getDirectory() {
        return directory;
    }

    /**
     * @return the pageVersions
     */
    public List<PageVersion> getPageVersions() {
        return pageVersions;
    }

    /**
     * @return the comments
     */
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * @return the categories
     */
    public Set<Category> getCategories() {
        return categories;
    }

    /**
     * @return the tags
     */
    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * @return the userPagePermissions
     */
    public Set<UserPagePermission> getUserPagePermissions() {
        return userPagePermissions;
    }

    /**
     * @return the userPageRatings
     */
    public Set<UserPageRating> getUserPageRatings() {
        return userPageRatings;
    }

}
