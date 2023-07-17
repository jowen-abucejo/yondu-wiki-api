package com.yondu.knowledgebase.entities;

import java.util.Objects;

import com.yondu.knowledgebase.entities.compositekeys.ReadPageId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class ReadPage {
    @EmbeddedId
    private ReadPageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pageId")
    private Page page;

    private ReadPage() {
    }

    public ReadPage(User user, Page page) {
        this.user = user;
        this.page = page;
        this.id = new ReadPageId(user.getId(), page.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ReadPage that = (ReadPage) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(page, that.page);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, page);
    }

    /**
     * @return the id
     */
    public ReadPageId getId() {
        return id;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @return the page
     */
    public Page getAddress() {
        return page;
    }

    /**
     * @param id the id to set
     */
    public void setId(ReadPageId id) {
        this.id = id;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @param page the page to set
     */
    public void setPage(Page page) {
        this.page = page;
    }

}
