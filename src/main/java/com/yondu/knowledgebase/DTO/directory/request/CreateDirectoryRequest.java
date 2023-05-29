package com.yondu.knowledgebase.DTO.directory.request;

import java.util.Objects;

public class CreateDirectoryRequest {
    private String name;
    private String description;

    public CreateDirectoryRequest() {}

    public CreateDirectoryRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return Objects.requireNonNullElse(this.name, "");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return Objects.requireNonNullElse(this.description, "");
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
