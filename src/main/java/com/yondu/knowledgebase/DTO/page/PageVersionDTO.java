package com.yondu.knowledgebase.DTO.page;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import jakarta.validation.constraints.NotBlank;

public class PageVersionDTO {
    @JsonInclude(Include.NON_EMPTY)
    private Long id;

    @NotBlank(message = "Page title cannot be empty.")
    @JsonInclude(Include.ALWAYS)
    private String title;

    @NotBlank(message = "Page content is required.")
    @JsonInclude(Include.ALWAYS)
    private String content;

    @JsonInclude(Include.ALWAYS)
    @JsonProperty(value = "last_edited_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateModified;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = "last_edited_by")
    private UserDTO modifiedBy;

    // @JsonInclude(Include.NON_EMPTY)
    // private List<ReviewDTO> reviews = new ArrayList<>();

    @JsonInclude(Include.NON_NULL)
    private PageDTO page = null;

    /**
     * @param title
     * @param content
     */
    @JsonCreator(mode = Mode.PROPERTIES)
    public PageVersionDTO(@JsonProperty(value = "title") String title,
            @JsonProperty(value = "content") String content) {
        this.title = title;
        this.content = content;
    }

    private PageVersionDTO(PageVersionDTOBuilder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.content = builder.content;
        this.dateModified = builder.dateModified;
        this.modifiedBy = builder.modifiedBy;
        this.page = builder.page;
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
     * @return the page
     */
    public PageDTO getPage() {
        return page;
    }

    public static class PageVersionDTOBuilder {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime dateModified;
        private UserDTO modifiedBy;
        private PageDTO page;

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

        public PageVersionDTO build() {
            return new PageVersionDTO(this);
        }

    }

}
