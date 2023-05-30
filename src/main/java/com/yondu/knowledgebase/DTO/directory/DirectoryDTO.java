package com.yondu.knowledgebase.DTO.directory;

import java.time.LocalDate;
import java.util.Set;

public class DirectoryDTO {
    public record CreateRequest(String name, String description){}
    public record RenameRequest(String name) {}
    public record BaseResponse(Long id, String name, String description, LocalDate dateCreated, java.time.LocalDate dateModified, String fullPath){}
    public record GetResponse(Long id, String name, String description, LocalDate dateCreated, java.time.LocalDate dateModified, String fullPath, Set<BaseResponse> subdirectories){}
}
