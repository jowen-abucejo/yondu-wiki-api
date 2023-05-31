package com.yondu.knowledgebase.enums;

public enum Permission {

    CREATE_USERS(1, "CREATE_USERS", "Create users", "User"),
    UPDATE_USERS(2, "UPDATE_USERS", "Update users", "User"),
    DEACTIVATE_USERS(3, "DEACTIVATE_USERS", "Deactivate users", "User"),
    VIEWER_USERS(4, "VIEW_USERS", "View users", "User"),

    CREATE_CONTENT(5, "CREATE_CONTENT", "Allows user to create content", "Content"),
    UPDATE_CONTENT(6, "UPDATE_CONTENT", "Allows user to update created content", "Content"),
    DELETE_CONTENT(7, "DELETE_CONTENT", "Allows user to delete created content", "Content"),
    READ_CONTENT(8, "READ_CONTENT", "Allows user to read created contents", "Content"),

    CONTENT_APPROVAL(9, "CONTENT_APPROVAL", "Allows user to approve created content", "Content Moderation"),

    COMMENT_AVAILABILITY(10, "COMMENT_AVAILABILITY", "Allows user to enable or disable content", "Comment"),
    CREATE_COMMENT(11, "CREATE_COMMENT", "Allows user to create comment in a page", "Comment"),
    UPDATE_COMMENT(12, "UPDATE_COMMENT", "Allows user to update comment in a page", "Comment"),
    DELETE_COMMENT(13, "DELETE_COMMENT", "Allows user to delete their own comment", "Comment"),
    VIEW_COMMENTS(14, "VIEW_COMMENTS", "Allows user to view comments in the page", "Comment"),

    UPDATE_PAGE_EDITOR(15, "UPDATE_PAGE_EDITOR", "Allow user to add/remove page editors in a page", "Page Editor"),

    CREATE_DIRECTOR(16, "CREATE_DIRECTORY", "Allow user to create directory", "Directory"),
    UPDATE_DIRECTORY(17, "UPDATE_DIRECTORY", "Allow user to create directory", "Directory"),
    DELETE_DIRECTORY(18, "DELETE_DIRECTORY", "Allow user to create directory", "Directory"),
    VIEW_DIRECTORY(19, "VIEW_DIRECTORY", "Allow user to create directory", "Directory"),
    MANAGE_DIRECTORY_PERMISSION(25, "MANAGE_DIRECTOR_PERMISSIONS", "Allow user to manage the directory permissions", "Directory"),

    CREATE_ROLES(20, "CREATE_ROLES", "Allow user to create new roles", "Roles"),
    UPDATE_ROLES(21, "UPDATE_ROLES", "Allow user to update roles", "Roles"),
    DELETE_ROLES(22, "DELETE_ROLES", "Allow user to delete roles", "Roles"),
    MANAGE_ROLES(23, "MANAGE_ROLES", "Allow user to manage roles", "Roles"),
    READ_ROLES(24, "READ_ROLES", "Allow user to view roles", "Roles");

    long id;
    String code;
    String description;
    String category;

    Permission(long id, String code, String description, String category) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
