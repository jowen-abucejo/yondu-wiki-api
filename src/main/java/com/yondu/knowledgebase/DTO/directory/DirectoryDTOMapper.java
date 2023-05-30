package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Directory;

import java.util.stream.Collectors;

public class DirectoryDTOMapper {

    public static DirectoryDTO.BaseResponse mapToBaseResponse(Directory directory) {
        return new DirectoryDTO.BaseResponse(
                directory.getId(),
                directory.getName(),
                directory.getDescription(),
                UserDTOMapper.mapToGeneralResponse(directory.getCreatedBy()),
                directory.getDateCreated(),
                directory.getDateModified(),
                directory.getFullPath());
    }

    public static DirectoryDTO.GetResponse mapToGetResponse(Directory directory) {
        return new DirectoryDTO.GetResponse(
                directory.getId(),
                directory.getName(),
                directory.getDescription(),
                UserDTOMapper.mapToGeneralResponse(directory.getCreatedBy()),
                directory.getDateCreated(),
                directory.getDateModified(),
                directory.getFullPath(),
                directory.getSubDirectories().stream().map(DirectoryDTOMapper::mapToBaseResponse).collect(Collectors.toSet())
        );
    }

}
