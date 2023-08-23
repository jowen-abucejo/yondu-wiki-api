package com.yondu.knowledgebase.enums;

public enum PageType {
    WIKI("WIKI"),
    ANNOUNCEMENT("ANNOUNCEMENT"),
    DISCUSSION("DISCUSSION");

    String code;

    PageType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
