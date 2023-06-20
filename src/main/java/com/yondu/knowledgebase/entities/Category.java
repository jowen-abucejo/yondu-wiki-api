package com.yondu.knowledgebase.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;


@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Category {

   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "is_deleted")
    private Boolean deleted =false;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "page_category", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns =@JoinColumn (name = "page_id"))
    private List<Page> pages = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "post_category", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns =@JoinColumn (name = "post_id"))
    private List<Post> posts = new ArrayList<>();

    public Category(){

    }

    public Category(Long id, String name, Boolean deleted, List<Page> pages, List<Post> posts) {
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

    public void setPages(List<Page> pages) {
        this.pages=pages;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
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
