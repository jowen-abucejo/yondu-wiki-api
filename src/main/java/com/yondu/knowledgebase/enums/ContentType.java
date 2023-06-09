package com.yondu.knowledgebase.enums;

public enum ContentType {

    PAGE("PAGE"),
    COMMENT("COMMENT"),
    POST("POST");

    String code;

    ContentType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
