package com.yondu.knowledgebase.DTO.Comment;

import com.yondu.knowledgebase.DTO.Response;

import java.util.List;

public class CommentResponseDTO {
    private List<CommentRequestDTO> data;
    private Response response;

    // Getter and Setter
    public List<CommentRequestDTO> getData() { return data; }

    public void setData(List<CommentRequestDTO> data) { this.data = data; }

    public Response getResponse() {return response; }

    // Constructor

    public void setResponse(Response response) { this.response = response; }

    public CommentResponseDTO() {}
    public CommentResponseDTO(List<CommentRequestDTO> data, Response response) {
        this.data = data;
        this.response = response;
    }
}

