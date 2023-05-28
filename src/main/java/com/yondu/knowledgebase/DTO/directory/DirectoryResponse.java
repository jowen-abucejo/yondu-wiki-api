package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.entities.Directory;

import java.util.ArrayList;
import java.util.List;

public class DirectoryResponse {
    private Long id;
    private String name;
    private Long parentId;
    private String fullPath;

    public DirectoryResponse() {}

    public DirectoryResponse(Directory directory) {
        this.id = directory.getId();
        this.name = directory.getName();
        if (directory.getParent() != null) {
            this.parentId = directory.getParent().getId();
            this.fullPath = getFullPath(directory);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public static String getFullPath(Directory directory) {
        if (directory == null) {
            return "";
        }

        return getFullPath(directory.getParent()) + "/" + directory.getName();
    }
}
