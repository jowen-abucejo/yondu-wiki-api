package com.yondu.knowledgebase.DTO.Comment;

import com.yondu.knowledgebase.DTO.Response;

import java.util.List;

public class CommentResponseDTO {
    private List<CommentRequestDTO> data;
    private Response response;

    private int totalComment;

    // Getter and Setter
    public List<CommentRequestDTO> getData() { return data; }

    public void setData(List<CommentRequestDTO> data) { this.data = data; }

    public int getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public Response getResponse() {return response; }

    // Constructor

    public void setResponse(Response response) { this.response = response; }

    public CommentResponseDTO() {}
    public CommentResponseDTO(List<CommentRequestDTO> data, Response response, int totalComment) {
        this.data = data;
        this.totalComment = totalComment;
        this.response = response;
    }
}

