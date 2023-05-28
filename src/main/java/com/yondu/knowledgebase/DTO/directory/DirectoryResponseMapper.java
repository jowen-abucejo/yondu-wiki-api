package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.entities.Directory;


public class DirectoryResponseMapper {

    public DirectoryResponse.Create mapToCreateDirectory(Directory directory) {
        return new DirectoryResponse.Create(directory.getId(), directory.getName(), directory.getDescription(), directory.getDateCreated(), directory.getDateModified());
    }

    public DirectoryResponse.Rename mapToRenameDirectory(Directory directory) {
        return new DirectoryResponse.Rename(directory.getId(), directory.getName(), directory.getDescription(), directory.getDateCreated(), directory.getDateModified());
    }
}
