package com.yondu.knowledgebase.DTO.page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PageDTO {

    @JsonInclude(Include.NON_EMPTY)
    private Long id;

    @JsonInclude(Include.ALWAYS)
    @JsonProperty(value = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreated;

    @JsonInclude(Include.ALWAYS)
    private UserDTO author;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = "not_archived")
    private Boolean active;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = "commenting_on")
    private Boolean allowComment;

    @JsonInclude(Include.NON_NULL)
    private Boolean deleted;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = "locked_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lockStart;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = "unlocked_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lockEnd;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = "key_holder")
    private UserDTO lockedBy;

    @JsonInclude(Include.NON_NULL)
    private BigDecimal relevance;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = "total_comments")
    private Long totalComments;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = "total_ratings")
    private Long totalRatings;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = "categories")
    private String[] categories;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = "tags")
    private String[] tags;

    @JsonInclude(Include.NON_EMPTY)
    private List<PageVersionDTO> versions = new ArrayList<>();

    @JsonInclude(Include.NON_EMPTY)
    private PageVersionDTO body;

    public PageDTO(PageDTOBuilder builder) {
        this.id = builder.id;
        this.dateCreated = builder.dateCreated;
        this.author = builder.author;
        this.active = builder.active;
        this.deleted = builder.deleted;
        this.allowComment = builder.allowComment;
        this.lockStart = builder.lockStart;
        this.lockEnd = builder.lockEnd;
        this.lockedBy = builder.lockedBy;
        this.versions = builder.versions;
        this.body = builder.body;
        this.relevance = builder.relevance;
        this.totalComments = builder.totalComments;
        this.totalRatings = builder.totalRatings;
        this.categories = builder.categories;
        this.tags = builder.tags;
    }

    public PageDTO() {
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
    public UserDTO getAuthor() {
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
    public UserDTO getLockedBy() {
        return lockedBy;
    }

    /**
     * @return the versions
     */
    public List<PageVersionDTO> getVersions() {
        return versions;
    }

    /**
     * @return the body
     */
    public PageVersionDTO getBody() {
        return body;
    }

    public static PageDTOBuilder builder() {
        return new PageDTOBuilder();
    }

    /**
     * @return the allowComment
     */
    public Boolean getAllowComment() {
        return allowComment;
    }

    /**
     * @return the relevance
     */
    public BigDecimal getRelevance() {
        return relevance;
    }

    /**
     * @return the totalComments
     */
    public Long getTotalComments() {
        return totalComments;
    }

    /**
     * @return the totalRatings
     */
    public Long getTotalRatings() {
        return totalRatings;
    }

    /**
     * @return the categories
     */
    public String[] getCategories() {
        return categories;
    }

    /**
     * @return the tags
     */
    public String[] getTags() {
        return tags;
    }

    public static class PageDTOBuilder {
        private BigDecimal relevance;
        private Long id;
        private LocalDateTime dateCreated;
        private UserDTO author;
        private Boolean active;
        private Boolean deleted;
        private Boolean allowComment;
        private LocalDateTime lockStart;
        private LocalDateTime lockEnd;
        private UserDTO lockedBy;
        private Long totalComments;
        private Long totalRatings;
        private String[] categories;
        private String[] tags;

        private List<PageVersionDTO> versions = new ArrayList<>();
        PageVersionDTO body;

        public PageDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PageDTOBuilder dateCreated(LocalDateTime dateCreated) {
            this.dateCreated = dateCreated;
            return this;
        }

        public PageDTOBuilder author(UserDTO author) {
            this.author = author;
            return this;
        }

        public PageDTOBuilder active(Boolean active) {
            this.active = active;
            return this;
        }

        public PageDTOBuilder deleted(Boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public PageDTOBuilder allowComment(Boolean allowComment) {
            this.allowComment = allowComment;
            return this;
        }

        public PageDTOBuilder lockStart(LocalDateTime lockStart) {
            this.lockStart = lockStart;
            return this;
        }

        public PageDTOBuilder lockEnd(LocalDateTime lockEnd) {
            this.lockEnd = lockEnd;
            return this;
        }

        public PageDTOBuilder lockedBy(UserDTO lockedBy) {
            this.lockedBy = lockedBy;
            return this;
        }

        public PageDTOBuilder versions(List<PageVersionDTO> versions) {
            this.versions = versions;
            return this;
        }

        public PageDTOBuilder body(PageVersionDTO body) {
            this.body = body;
            return this;
        }

        public PageDTOBuilder relevance(BigDecimal relevance) {
            this.relevance = relevance;
            return this;
        }

        public PageDTOBuilder totalComments(Long totalComments) {
            this.totalComments = totalComments;
            return this;
        }

        public PageDTOBuilder totalRatings(Long totalRatings) {
            this.totalRatings = totalRatings;
            return this;
        }

        public PageDTOBuilder categories(String[] categories) {
            this.categories = categories;
            return this;
        }

        public PageDTOBuilder tags(String[] tags) {
            this.tags = tags;
            return this;
        }

        public PageDTO build() {
            return new PageDTO(this);
        }
    }
}
