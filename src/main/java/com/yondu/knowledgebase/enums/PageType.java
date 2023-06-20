package com.yondu.knowledgebase.enums;

public enum PageType {
    WIKI("WIKI"),
    ANNOUNCEMENT("ANNOUNCEMENT");

    String code;

    PageType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
