package com.yondu.knowledgebase.enums;

public enum EntityType {

    PAGE("PAGE"),
    POST("POST"),
    COMMENT("COMMENT");

    String code;

    EntityType(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
