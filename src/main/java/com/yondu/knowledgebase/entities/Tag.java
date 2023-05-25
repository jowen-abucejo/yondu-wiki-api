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

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "page_tag", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns =@JoinColumn (name = "page_id"))
    private List<Page> pages = new ArrayList<>();

    public Tag(){

    }

    public Tag(Long id, String name, List<Page> pages){
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
}
