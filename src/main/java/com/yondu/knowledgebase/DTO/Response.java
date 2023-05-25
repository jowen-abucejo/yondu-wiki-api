package com.yondu.knowledgebase.DTO;

public class Response {
    private int code;
    private String message;

    // Getter and Setter
    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    // Constructor
    public Response() { }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
