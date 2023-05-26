package com.yondu.knowledgebase.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class PageVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @LastModifiedDate
    private LocalDateTime dateModified;

    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "modified_by", referencedColumnName = "id")
    private User modifiedBy;

    @OneToMany(mappedBy = "pageVersion")
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "page_id", referencedColumnName = "id")
    private Page page;

    /**
    *
    */
    public PageVersion() {
    }

    /**
     * @param id
     * @param title
     * @param content
     * @param dateModified
     * @param modifiedBy
     * @param reviews
     */
    public PageVersion(Long id, String title, String content, LocalDateTime dateModified, User modifiedBy,
            List<Review> reviews) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.dateModified = dateModified;
        this.modifiedBy = modifiedBy;
        this.reviews = reviews;
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
    public User getModifiedBy() {
        return modifiedBy;
    }

    /**
     * @return the reviews
     */
    public List<Review> getReviews() {
        return reviews;
    }

    /**
     * @return the page
     */
    public Page getPage() {
        return page;
    }

}
