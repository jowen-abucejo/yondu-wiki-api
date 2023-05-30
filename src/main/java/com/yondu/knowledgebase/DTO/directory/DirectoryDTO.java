package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.DTO.user.UserDTO;

import java.time.LocalDate;
import java.util.Set;

public class DirectoryDTO {
    public record CreateRequest(String name, String description){}
    public record RenameRequest(String name) {}
    public record BaseResponse(Long id,
                               String name,
                               String description,
                               UserDTO.GeneralResponse createdBy,
                               LocalDate dateCreated,
                               LocalDate dateModified){}
    public record GetResponse(Long id,
                              String name,
                              String description,
                              UserDTO.GeneralResponse createdBy,
                              LocalDate dateCreated,
                              LocalDate dateModified,
                              Set<BaseResponse> subdirectories){}
}
