package com.yondu.knowledgebase.enums;

public enum NotificationType {

    GENERAL("GENERAL"),
    COMMENT("COMMENT"),
    APPROVAL("APPROVAL"),
    MENTION("MENTION"),
    RATE("RATE"),
    CREATION("CREATION");

    String code;

    NotificationType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
