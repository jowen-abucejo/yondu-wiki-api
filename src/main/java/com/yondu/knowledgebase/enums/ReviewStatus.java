package com.yondu.knowledgebase.enums;

public enum ReviewStatus {

    PENDING("PENDING"),
    APPROVED("APPROVED"),
    DISAPPROVED("DISAPPROVED");

    String code;

    ReviewStatus(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }


}
