package com.yondu.knowledgebase.DTO.post;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.yondu.knowledgebase.entities.Post;

public class PostSearchResult extends PostDTO {
    @JsonInclude(Include.NON_NULL)
    private BigDecimal relevance;

    /**
     * @param relevance
     * @param totalComments
     * @param totalRatings
     * @param post
     */
    public PostSearchResult(Post post, BigDecimal relevance, Long totalComments, Long totalRatings) {
        super(post, totalComments, totalRatings);
        this.relevance = relevance;
    }

    /**
     * @return the relevance
     */
    public BigDecimal getRelevance() {
        return relevance;
    }

}
