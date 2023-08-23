package com.yondu.knowledgebase.DTO.page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PageDTO {

    @JsonInclude(Include.NON_EMPTY)
    private Long id;

    @JsonInclude(Include.ALWAYS)
    @JsonProperty(value = "created_at")
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
    private LocalDateTime lockStart;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = "unlocked_at")
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
    @JsonProperty(value = "total_parent_comments")
    private Long totalParentComments;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = "total_ratings")
    private Long totalRatings;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = "total_down_ratings")
    private Long totalDownRatings;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = "categories")
    private String[] categories;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = "tags")
    private String[] tags;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = "user_page_permissions")
    private long[] pagePermissions;

    @JsonInclude(Include.NON_EMPTY)
    private List<PageVersionDTO> versions = new ArrayList<>();

    @JsonInclude(Include.NON_EMPTY)
    private PageVersionDTO body;

    @JsonProperty(value = "type")
    private String pageType;

    private Long directoryId;
    private String directoryName;
    private Long directoryWorkflowId;
    private Long directoryWorkflowStepCount;

    private Boolean saved;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = "saved_at")
    private LocalDateTime dateSaved;

    @JsonProperty(value = "vote")
    private String myRating;

    private PageDTO(PageDTOBuilder builder) {
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
        this.pageType = builder.pageType;
        this.directoryId = builder.directoryId;
        this.directoryName = builder.directoryName;
        this.directoryWorkflowId = builder.directoryWorkflowId;
        this.directoryWorkflowStepCount = builder.directoryWorkflowStepCount;
        this.saved = builder.saved;
        this.myRating = builder.myRating;
        this.pagePermissions = builder.pagePermissions;
        this.dateSaved = builder.dateSaved;
        this.totalDownRatings = builder.totalDownRatings;
        this.totalParentComments = builder.totalParentComments;
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
     * @return the totalParentComments
     */
    public Long getTotalParentComments() {
        return totalParentComments;
    }

    /**
     * @return the totalRatings
     */
    public Long getTotalRatings() {
        return totalRatings;
    }

    /**
     * @return the totalDownRatings
     */
    public Long getTotalDownRatings() {
        return totalDownRatings;
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

    /**
     * @return the pagePermissions
     */
    public long[] getPagePermissions() {
        return pagePermissions;
    }

    public static class PageDTOBuilder {
        private String pageType;
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
        private Long directoryId;
        private Long directoryWorkflowId;
        private Long directoryWorkflowStepCount;
        private String directoryName;
        private Boolean saved;
        private String myRating;
        private long[] pagePermissions;
        private LocalDateTime dateSaved;
        private Long totalDownRatings;
        private Long totalParentComments;

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

        public PageDTOBuilder totalParentComments(Long totalParentComments) {
            this.totalParentComments = totalParentComments;
            return this;
        }

        public PageDTOBuilder totalRatings(Long totalRatings) {
            this.totalRatings = totalRatings;
            return this;
        }

        public PageDTOBuilder totalDownRatings(Long totalDownRatings) {
            this.totalDownRatings = totalDownRatings;
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

        public PageDTOBuilder pageType(String pageType) {
            this.pageType = StringUtils.capitalize(pageType.toLowerCase());
            return this;
        }

        public PageDTOBuilder directoryName(String directoryName) {
            this.directoryName = directoryName;
            return this;
        }

        public PageDTOBuilder directoryId(Long directoryId) {
            this.directoryId = directoryId;
            return this;
        }

        public PageDTOBuilder directoryWorkflowId(Long directoryWorkflowId) {
            this.directoryWorkflowId = directoryWorkflowId;
            return this;
        }

        public PageDTOBuilder directoryWorkflowStepCount(Long directoryWorkflowStepCount) {
            this.directoryWorkflowStepCount = directoryWorkflowStepCount;
            return this;
        }

        public PageDTOBuilder saved(Boolean saved) {
            this.saved = saved;
            return this;
        }

        public PageDTOBuilder myRating(String myRating) {
            this.myRating = myRating;
            return this;
        }

        public PageDTOBuilder pagePermissions(long[] pagePermissions) {
            this.pagePermissions = pagePermissions;
            return this;
        }

        public PageDTOBuilder dateSaved(LocalDateTime dateSaved) {
            this.dateSaved = dateSaved;
            return this;
        }

        public PageDTO build() {
            return new PageDTO(this);
        }
    }

    /**
     * @return the pageType
     */
    public String getPageType() {
        return pageType;
    }

    /**
     * @return the directoryId
     */
    public Long getDirectoryId() {
        return directoryId;
    }

    /**
     * @return the directoryName
     */
    public String getDirectoryName() {
        return directoryName;
    }

    /**
     * @return the directoryWorkflowId
     */
    public Long getDirectoryWorkflowId() {
        return directoryWorkflowId;
    }

    /**
     * @return the directoryWorkflowStepCount
     */
    public Long getDirectoryWorkflowStepCount() {
        return directoryWorkflowStepCount;
    }

    /**
     * @return the saved
     */
    public Boolean getSaved() {
        return saved;
    }

    /**
     * @return the myRating
     */
    public String getMyRating() {
        return myRating;
    }

    /**
     * @return the dateSaved
     */
    public LocalDateTime getDateSaved() {
        return dateSaved;
    }

}
