package com.yondu.knowledgebase.DTO.tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yondu.knowledgebase.DTO.page_permission.PageDTO;
import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.entities.Tag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

public class TagDTO {

    private Long id;
    private String name;

    @JsonInclude
    private List<PageDTO> pages = new ArrayList<>();

    private Boolean deleted = false;

    public TagDTO() {

    }

    public TagDTO(Long id, String name, Boolean deleted, List<PageDTO> pages) {
        this.id = id;
        this.name = name;
        this.deleted = deleted;
        this.pages = pages;
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

    public Boolean getDeleted() {
        return deleted;
    }
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public List<PageDTO> getPages() {
        return pages;
    }


}
