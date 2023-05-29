package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.entities.Directory;


public class DirectoryResponseMapper {
    public DirectoryResponse.Create mapToCreateDirectory(Directory directory) {
        return new DirectoryResponse.Create(directory.getId(), directory.getName(), directory.getDescription(), directory.getDateCreated(), directory.getDateModified(), getFullPath(directory));
    }

    public static String getFullPath(Directory directory) {
        if (directory.getParent() == null) {
            return directory.getName();
        }
        return getFullPath(directory.getParent()) + "/" + directory.getName();
    }
}
