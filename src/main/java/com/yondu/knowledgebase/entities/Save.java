package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
public class Save {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "author", referencedColumnName = "id", updatable = false, nullable = false)
    private User author;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime dateCreated;

    private String entityType;

    private Long entityId;

    public Save(User author, String entityType, Long entityId) {
        this.id = id;
        this.author = author;
        this.dateCreated = dateCreated;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public Save() {
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

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    @Override
    public String toString() {
        return "Save{" +
                "id=" + id +
                ", author=" + author +
                ", dateCreated=" + dateCreated +
                ", entityType='" + entityType + '\'' +
                ", entityId=" + entityId +
                '}';
    }
}
