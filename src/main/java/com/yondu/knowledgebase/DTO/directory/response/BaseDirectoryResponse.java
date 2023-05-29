package com.yondu.knowledgebase.DTO.directory.response;

import com.yondu.knowledgebase.entities.Directory;

import java.time.LocalDate;

public class BaseDirectoryResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate dateCreated;
    private LocalDate dateModified;

    public BaseDirectoryResponse() {}

    public BaseDirectoryResponse(Directory directory) {
        this.id = directory.getId();
        this.name = directory.getName();
        this.description = directory.getDescription();
        this.dateCreated = directory.getDateCreated();
        this.dateModified = directory.getDateModified();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }
}
