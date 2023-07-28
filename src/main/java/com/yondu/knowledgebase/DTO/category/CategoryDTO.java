package com.yondu.knowledgebase.DTO.category;

public class CategoryDTO {

    private Long id;
    private String name;
    private Boolean deleted;

    public CategoryDTO() {

    }

    public CategoryDTO(Long id, String name, Boolean deleted) {
        this.id = id;
        this.name = name;
        this.deleted = deleted;

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
