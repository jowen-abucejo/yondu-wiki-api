package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.DTO.user.UserDTOMapper;
import com.yondu.knowledgebase.entities.Directory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DirectoryDTOMapper {

    public static DirectoryDTO.BaseResponse mapToBaseResponse(Directory directory) {
        return new DirectoryDTO.BaseResponse(
                directory.getId(),
                directory.getName(),
                directory.getDescription(),
                UserDTOMapper.mapToGeneralResponse(directory.getCreatedBy()),
                directory.getDateCreated(),
                directory.getDateModified());
    }

    public static DirectoryDTO.GetResponse mapToGetResponse(Directory directory) {
        return new DirectoryDTO.GetResponse(
                directory.getId(),
                directory.getName(),
                directory.getDescription(),
                UserDTOMapper.mapToGeneralResponse(directory.getCreatedBy()),
                directory.getDateCreated(),
                directory.getDateModified(),
                traverse(directory),
                directory.getSubDirectories().stream().map(DirectoryDTOMapper::mapToBaseResponse).collect(Collectors.toSet())
        );
    }

    public static DirectoryDTO.ShortResponse mapToShortResponse(Directory directory) {
        return new DirectoryDTO.ShortResponse(
                directory.getId(),
                directory.getName(),
                directory.getDescription());
    }

    public static DirectoryDTO.Response mapToResponse(Directory directory) {
        return new DirectoryDTO.Response(
                directory.getId(),
                directory.getName(),
                directory.getDescription(),
                UserDTOMapper.mapToGeneralResponse(directory.getCreatedBy()),
                directory.getDateCreated(),
                directory.getDateModified(),
                getParentDirectories(directory),
                directory.getSubDirectories().stream().map(DirectoryDTOMapper::mapToBaseResponse).collect(Collectors.toSet())
        );
    }

    private static String traverse(Directory directory) {
        if (directory.getParent() == null) return directory.getId() + "->" + directory.getName();
        return traverse(directory.getParent()) + "/" + directory.getId() + "->" + directory.getName();
    }

    private static List<List<String>> getParentDirectories(Directory directory) {
        List<List<String>> parentDirectories = new ArrayList<>();
        Directory currentDirectory = directory.getParent();

        while (currentDirectory != null) {
            List<String> directoryInfo = new ArrayList<>();
            directoryInfo.add("ID: " + currentDirectory.getId());
            directoryInfo.add("Name: " + currentDirectory.getName());
            parentDirectories.add(directoryInfo);
            currentDirectory = currentDirectory.getParent();
        }

        return parentDirectories;
    }
}
