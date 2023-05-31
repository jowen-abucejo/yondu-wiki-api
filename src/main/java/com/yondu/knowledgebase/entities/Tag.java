package com.yondu.knowledgebase.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(name = "is_deleted")
    private Boolean deleted = false;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "page_tag", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns =@JoinColumn (name = "page_id"))
    private List<Page> pages = new ArrayList<>();

    public Tag(){

    }

    public Tag(Long id, String name, Boolean deleted, List<Page> pages){
        this.id= id;
        this.name = name;
        this.deleted = deleted;
        this.pages = pages;
    }

    public Long getId(){
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName(){
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

    public List<Page> getPages(){
        return pages;
    }
}
