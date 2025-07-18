package com.yondu.knowledgebase.DTO.page;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public class PageVersionDTO {
    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = "version_id")
    private Long id;

    @NotBlank(message = "Page title cannot be empty.")
    @JsonInclude(Include.ALWAYS)
    private String title;

    @JsonInclude(Include.ALWAYS)
    private String content;

    @JsonInclude(Include.ALWAYS)
    @JsonProperty(value = "last_edited_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateModified;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = "last_edited_by")
    private UserDTO modifiedBy;

    @JsonProperty(value = "approved_reviews_count")
    private Long totalApprovedReviews;

    @JsonProperty(value = "disapproved_reviews_count")
    private Long totalDisapprovedReviews;

    @JsonInclude(Include.NON_NULL)
    private PageDTO page = null;

    @JsonInclude(Include.NON_EMPTY)
    private String[] tags;

    @JsonInclude(Include.NON_EMPTY)
    private String[] categories;

    private Boolean isDraft;

    /**
     * @param title
     * @param content
     * @param tags
     * @param categories
     */
    public PageVersionDTO(@JsonProperty(value = "title") String title,
            @JsonProperty(value = "content") String content, @JsonProperty(value = "tags") String[] tags,
            @JsonProperty(value = "categories") String[] categories) {
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.categories = categories;
    }

    private PageVersionDTO(PageVersionDTOBuilder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.content = builder.content;
        this.dateModified = builder.dateModified;
        this.modifiedBy = builder.modifiedBy;
        this.page = builder.page;
        this.totalApprovedReviews = builder.totalApprovedReviews;
        this.totalDisapprovedReviews = builder.totalDisapprovedReviews;
        this.isDraft = builder.isDraft;
    }

    public static PageVersionDTOBuilder builder() {
        return new PageVersionDTOBuilder();
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @return the dateModified
     */
    public LocalDateTime getDateModified() {
        return dateModified;
    }

    /**
     * @return the modifiedBy
     */
    public UserDTO getModifiedBy() {
        return modifiedBy;
    }

    /**
     * @return the totalApprovedReviews
     */
    public Long getTotalApprovedReviews() {
        return totalApprovedReviews;
    }

    /**
     * @return the totalDisapprovedReviews
     */
    public Long getTotalDisapprovedReviews() {
        return totalDisapprovedReviews;
    }

    /**
     * @return the isDraft
     */
    public Boolean getIsDraft() {
        return isDraft;
    }

    /**
     * @return the page
     */
    public PageDTO getPage() {
        return page;
    }

    /**
     * @return the tags
     */
    public String[] getTags() {
        return tags;
    }

    /**
     * @return the categories
     */
    public String[] getCategories() {
        return categories;
    }

    public static class PageVersionDTOBuilder {
        public Boolean isDraft;
        private Long id;
        private String title;
        private String content;
        private LocalDateTime dateModified;
        private UserDTO modifiedBy;
        private PageDTO page;
        private Long totalApprovedReviews;
        private Long totalDisapprovedReviews;

        public PageVersionDTOBuilder() {
        }

        public PageVersionDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PageVersionDTOBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PageVersionDTOBuilder content(String content) {
            this.content = content;
            return this;
        }

        public PageVersionDTOBuilder dateModified(LocalDateTime dateModified) {
            this.dateModified = dateModified;
            return this;
        }

        public PageVersionDTOBuilder modifiedBy(UserDTO modifiedBy) {
            this.modifiedBy = modifiedBy;
            return this;
        }

        public PageVersionDTOBuilder page(PageDTO page) {
            this.page = page;
            return this;
        }

        public PageVersionDTOBuilder totalApprovedReviews(Long totalApprovedReviews) {
            this.totalApprovedReviews = totalApprovedReviews;
            return this;
        }

        public PageVersionDTOBuilder totalDisapprovedReviews(Long totalDisapprovedReviews) {
            this.totalDisapprovedReviews = totalDisapprovedReviews;
            return this;
        }

        public PageVersionDTOBuilder isDraft(Boolean isDraft) {
            this.isDraft = isDraft;
            return this;
        }

        public PageVersionDTO build() {
            return new PageVersionDTO(this);
        }

    }
}
