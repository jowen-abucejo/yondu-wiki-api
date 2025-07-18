package com.yondu.knowledgebase.enums;

public enum ContentType {

    PAGE("PAGE"),
    ANNOUNCEMENT("ANNOUNCEMENT"),
    WIKI("WIKI"),
    POST("POST"),
    REPLY("COMMENT");

    String code;

    ContentType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
