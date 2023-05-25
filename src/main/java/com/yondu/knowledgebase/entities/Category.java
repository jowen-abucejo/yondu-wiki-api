package com.yondu.knowledgebase.entities;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy ="category")
    private List<PageCategory> pageCategory = new ArrayList<>();

    public Category(){

    }

    public Category(Long id, String name){
        this.id= id;
        this.name = name;
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name; 
    }
}
