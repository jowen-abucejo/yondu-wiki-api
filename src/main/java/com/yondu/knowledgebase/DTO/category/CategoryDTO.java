package com.yondu.knowledgebase.DTO.category;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.yondu.knowledgebase.DTO.page_permission.PageDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryDTO {


        private Long id;
        private String name;
        private Boolean deleted;
    
        @JsonInclude
        private List<PageDTO> pages = new ArrayList<>();
    
        public CategoryDTO(){
    
        }
    
        public CategoryDTO(Long id, String name, Boolean deleted, List<PageDTO> pages){
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
    
    
        public List<PageDTO> getPages(){
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
    }
    

