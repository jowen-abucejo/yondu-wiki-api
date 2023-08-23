package com.yondu.knowledgebase.DTO.tag;

public class TagDTO {

    private Long id;
    private String name;

    private Boolean deleted = false;

    public TagDTO() {

    }

    public TagDTO(Long id, String name, Boolean deleted) {
        this.id = id;
        this.name = name;
        this.deleted = deleted;
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

}
