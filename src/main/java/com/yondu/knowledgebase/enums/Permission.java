package com.yondu.knowledgebase.enums;

public enum Permission {
    CREATE_USERS(1, "User", "Create users", "CREATE_USERS"),
    UPDATE_USERS(2, "User", "Update users", "UPDATE_USERS"),
    DEACTIVATE_USERS(3, "User", "Deactivate users", "DEACTIVATE_USERS"),
    VIEW_USERS(4, "User", "View users", "VIEW_USERS"),
    CREATE_CONTENT(5, "Content", "Allows user to create content", "CREATE_CONTENT"),
    UPDATE_CONTENT(6, "Content", "Allows user to update created content", "UPDATE_CONTENT"),
    DELETE_CONTENT(7, "Content", "Allows user to delete created content", "DELETE_CONTENT"),
    READ_CONTENT(8, "Content", "Allows user to read created contents", "READ_CONTENT"),
    CONTENT_APPROVAL(9, "Content Moderation", "Allows user to approve created content", "CONTENT_APPROVAL"),
    COMMENT_AVAILABILITY(10, "Comment", "Allows user to enable or disable content", "COMMENT_AVAILABILITY"),
    CREATE_COMMENT(11, "Comment", "Allows user to create comment in a page", "CREATE_COMMENT"),
    UPDATE_COMMENT(12, "Comment", "Allows user to update comment in a page", "UPDATE_COMMENT"),
    DELETE_COMMENT(13, "Comment", "Allows user to delete their own comment", "DELETE_COMMENT"),
    VIEW_COMMENTS(14, "Comment", "Allows user to view comments in the page", "VIEW_COMMENTS"),
    UPDATE_PAGE_EDITOR(15, "Page Editor", "Allow user to add/remove page editors in a page", "UPDATE_PAGE_EDITOR"),
    CREATE_DIRECTORY(16, "Directory", "Allow user to create directory", "CREATE_DIRECTORY"),
    UPDATE_DIRECTORY(17, "Directory", "Allow user to update directory", "UPDATE_DIRECTORY"),
    DELETE_DIRECTORY(18, "Directory", "Allow user to delete directory", "DELETE_DIRECTORY"),
    VIEW_DIRECTORY(19, "Directory", "Allow user to view directory", "VIEW_DIRECTORY"),
    CREATE_ROLES(20, "Roles", "Allow user to create new roles", "CREATE_ROLES"),
    UPDATE_ROLES(21, "Roles", "Allow user to update roles", "UPDATE_ROLES"),
    DELETE_ROLES(22, "Roles", "Allow user to delete roles", "DELETE_ROLES"),
    MANAGE_ROLES(23, "Roles", "Allow user to manage roles", "MANAGE_ROLES"),
    READ_ROLES(24, "Roles", "Allow user to view roles", "READ_ROLES"),
    MANAGE_DIRECTORY_PERMISSIONS(25, "Directory", "Allow user to manage the directory permissions", "MANAGE_DIRECTORY_PERMISSIONS"),
    MANAGE_PAGE_PERMISSIONS(26, "Content", "Allow user to manage the page permissions", "MANAGE_PAGE_PERMISSIONS");

    private long id;
    private String category;
    private String description;
    private String code;

    Permission(long id, String category, String description, String code) {
        this.id = id;
        this.category = category;
        this.description = description;
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
