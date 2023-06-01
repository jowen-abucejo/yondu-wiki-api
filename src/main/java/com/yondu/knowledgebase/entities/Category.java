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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "page_category", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns =@JoinColumn (name = "page_id"))
    private List<Page> pages = new ArrayList<>();

    public Category(){

    }

    public Category(Long id, String name, Boolean deleted, List<Page> pages){
        this.id= id;
        this.name = name;
        this.deleted = deleted;
        this.pages = pages;
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
}
