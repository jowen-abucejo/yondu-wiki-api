package com.yondu.knowledgebase.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Category {

   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "page_category", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns =@JoinColumn (name = "page_id"))
    private List<Page> pages = new ArrayList<>();

    public Category(){

    }

    public Category(Long id, String name, List<Page> pages){
        this.id= id;
        this.name = name;
        this.pages = pages;
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name; 
    }

    public List<Page> getPages(){
        return pages;
    }

    public void setName(String name){
        this.name = name;
    }
}
