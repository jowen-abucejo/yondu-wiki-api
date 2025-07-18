package com.yondu.knowledgebase.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    @Column(updatable = false)
    private LocalDateTime dateCreated;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "author", referencedColumnName = "id", updatable = false)
    private User author;

    @Column(name = "page_type", updatable = false, columnDefinition = "VARCHAR(20) DEFAULT 'WIKI'", length = 20)
    private String type = "WIKI";

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active = true;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = false;

    @Column(name = "allow_comment", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean allowComment = true;

    @CreatedDate
    @Column(name = "lock_start", nullable = false)
    private LocalDateTime lockStart;

    @Column(name = "lock_end", nullable = false)
    private LocalDateTime lockEnd;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "locked_by", referencedColumnName = "id", nullable = false)
    private User lockedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_id", referencedColumnName = "id")
    private Directory directory;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    @OrderBy(value = "modified_by DESC")
    private List<PageVersion> pageVersions = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "page_category", joinColumns = @JoinColumn(name = "page_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "page_tag", joinColumns = @JoinColumn(name = "page_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "page")
    private Set<UserPageAccess> userPageAccess = new HashSet<>();

    @OneToMany(mappedBy = "page")
    private Set<GroupPageAccess> groupPageAccess = new HashSet<>();

    @OneToMany(mappedBy = "page")
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
     * @param type
     * @param active
     * @param deleted
     * @param allowComment
     * @param lockStart
     * @param lockEnd
     * @param lockedBy
     * @param directory
     * @param pageVersions
     * @param categories
     * @param tags
     * @param userPageAccess
     * @param groupPageAccess
     * @param userPageRatings
     */
    public Page(Long id, LocalDateTime dateCreated, User author, String type, Boolean active, Boolean deleted,
            Boolean allowComment, LocalDateTime lockStart, LocalDateTime lockEnd, User lockedBy, Directory directory,
            List<PageVersion> pageVersions, Set<Category> categories, Set<Tag> tags, Set<UserPageAccess> userPageAccess,
            Set<GroupPageAccess> groupPageAccess, Set<UserPageRating> userPageRatings) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.author = author;
        this.type = type;
        this.active = active;
        this.deleted = deleted;
        this.allowComment = allowComment;
        this.lockStart = lockStart;
        this.lockEnd = lockEnd;
        this.lockedBy = lockedBy;
        this.directory = directory;
        this.pageVersions = pageVersions;
        this.categories = categories;
        this.tags = tags;
        this.userPageAccess = userPageAccess;
        this.groupPageAccess = groupPageAccess;
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
     * @return the allowComment
     */
    public Boolean getAllowComment() {
        return allowComment;
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
     * @return the userPageAccess
     */
    public Set<UserPageAccess> getUserPageAccesses() {
        return userPageAccess;
    } // To delete

    /**
     * @return the groupPageAccess
     */
    public Set<GroupPageAccess> getGroupPageAccesses() {
        return groupPageAccess;
    }

    /**
     * @return the userPageRatings
     */
    public Set<UserPageRating> getUserPageRatings() {
        return userPageRatings;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(User author) {
        this.author = author;
    }

    /**
     * @param active the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @param allowComment the allowComment to set
     */
    public void setAllowComment(Boolean allowComment) {
        this.allowComment = allowComment;
    }

    /**
     * @param lockStart the lockStart to set
     */
    public void setLockStart(LocalDateTime lockStart) {
        this.lockStart = lockStart;
    }

    /**
     * @param lockEnd the lockEnd to set
     */
    public void setLockEnd(LocalDateTime lockEnd) {
        this.lockEnd = lockEnd;
    }

    /**
     * @param lockedBy the lockedBy to set
     */
    public void setLockedBy(User lockedBy) {
        this.lockedBy = lockedBy;
    }

    /**
     * @param directory the directory to set
     */
    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    /**
     * @param pageVersions the pageVersions to set
     */
    public void setPageVersions(List<PageVersion> pageVersions) {
        this.pageVersions = pageVersions;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    /**
     * @param userPageAccess the userPageAccess to set
     */
    public void setUserPageAccess(Set<UserPageAccess> userPageAccess) {
        this.userPageAccess = userPageAccess;
    }

    /**
     * @param groupPageAccess the userPageAccess to set
     */
    public void setGroupPageAccess(Set<GroupPageAccess> groupPageAccess) {
        this.groupPageAccess = groupPageAccess;
    }

    /**
     * @param userPageRatings the userPageRatings to set
     */
    public void setUserPageRatings(Set<UserPageRating> userPageRatings) {
        this.userPageRatings = userPageRatings;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the userPageAccess
     */
    public Set<UserPageAccess> getUserPageAccess() {
        return userPageAccess;
    }

    /**
     * @return the groupPageAccess
     */
    public Set<GroupPageAccess> getGroupPageAccess() {
        return groupPageAccess;
    }

}
