package com.yondu.knowledgebase.entities.compositekeys;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;

public class ReadPageId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "page_id")
    private Long pageId;

    private ReadPageId() {
    }

    public ReadPageId(
            Long userId,
            Long pageId) {
        this.userId = userId;
        this.pageId = pageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ReadPageId that = (ReadPageId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(pageId, that.pageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, pageId);
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @return the pageId
     */
    public Long getPageId() {
        return pageId;
    }

}
