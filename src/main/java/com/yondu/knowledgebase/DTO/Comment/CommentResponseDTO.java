package com.yondu.knowledgebase.DTO.Comment;

import com.yondu.knowledgebase.DTO.Response;
import com.yondu.knowledgebase.entities.Comment;

import java.util.List;

public class CommentResponseDTO {
    private List<CommentRequestDTO> data;
    private Long pageId;

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

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


    // Constructor

    public CommentResponseDTO() {}
    public CommentResponseDTO(List<CommentRequestDTO> data, int totalComment) {
        this.data = data;
        this.totalComment = totalComment;
    }
}

