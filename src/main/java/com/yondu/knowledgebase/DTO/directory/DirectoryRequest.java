package com.yondu.knowledgebase.DTO.directory;

public class DirectoryRequest {
    private String name;
    private String description;

    public DirectoryRequest() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
