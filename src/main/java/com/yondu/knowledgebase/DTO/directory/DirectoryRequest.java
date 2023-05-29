package com.yondu.knowledgebase.DTO.directory;

public class DirectoryRequest {
    public record Create(String name, String description){}
    public record Rename(String name) {}
}
