package com.yondu.knowledgebase.enums;

public enum Status {

    ACTIVE("ACT"),
    INACTIVE("INA");

    String code;

    Status(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
