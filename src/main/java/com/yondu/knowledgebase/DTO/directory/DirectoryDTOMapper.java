package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.entities.Directory;

public class DirectoryDTOMapper {

    public static DirectoryDTO.BaseResponse mapToBaseResponse(Directory directory) {
        return new DirectoryDTO.BaseResponse(directory.getId(), directory.getName(), directory.getDescription(), directory.getDateCreated(), directory.getDateModified(), "temp");
    }

}
