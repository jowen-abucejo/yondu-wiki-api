package com.yondu.knowledgebase.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity(name="page_category")
public class PageCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; 

    @ManyToOne
    @JoinColumn(name = "pageID")
    private Page page; 

    public PageCategory(){

    }

    public PageCategory(Long id, Category category, Page page){
        this.id = id; 
        this.category = category;
        this.page = page;
    }

    public Long getId(){
        return id;
    
    }

    public Category getCategory(){
        return category;
    }

    public Page getPage(){
        return page; 
    }
}
