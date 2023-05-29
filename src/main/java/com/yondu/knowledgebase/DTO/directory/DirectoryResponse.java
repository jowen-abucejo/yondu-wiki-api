package com.yondu.knowledgebase.DTO.directory;

import com.yondu.knowledgebase.entities.Directory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DirectoryResponse {
    public DirectoryResponse(Directory directory) {
    }
    public record Create(Long id, String name, String description, LocalDate dateCreated, LocalDate dateModified){}
    public record Rename(Long id, String name, String description, LocalDate dateCreated, LocalDate dateModified){}
}
