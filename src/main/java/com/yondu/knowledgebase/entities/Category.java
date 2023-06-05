package com.yondu.knowledgebase.entities;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Category {

   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(name = "is_deleted")
    private Boolean deleted =false;

    @ManyToMany(mappedBy = "categories")
    private List<Page> pages = new ArrayList<>();

    @ManyToMany(mappedBy = "categories")
    private List<Page> posts = new ArrayList<>();

    public Category(){

    }

    public Category(Long id, String name, Boolean deleted, List<Page> pages, List<Page> posts) {
        this.id = id;
        this.name = name;
        this.deleted = deleted;
        this.pages = pages;
        this.posts = posts;
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name; 
    }

    public Boolean getDeleted() {
        return deleted;
    }


    public List<Page> getPages(){
        return pages;
    }

    public void setId(Long id){
        this.id=id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDeleted(Boolean deleted){
        this.deleted = deleted;
    }

    public void setPages(List<Page> pages2) {
    }

    public List<Page> getPosts() {
        return posts;
    }

    public void setPosts(List<Page> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deleted=" + deleted +
                ", pages=" + pages +
                ", posts=" + posts +
                '}';
    }
}
