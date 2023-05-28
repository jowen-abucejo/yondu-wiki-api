package com.yondu.knowledgebase.DTO.directory.request;

import java.util.Objects;

public class RenameDirectoryRequest {
    private String name;

    public RenameDirectoryRequest() {}

    public String getName() {
        return Objects.requireNonNullElse(this.name, "");
    }

    public void setName(String name) {
        this.name = name;
    }
}
