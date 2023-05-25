package com.yondu.knowledgebase.entities;

import jakarta.persistence.*;

public class PageTag {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tags_id")
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id")
    private Page page;

    public PageTag() {
    }

    public PageTag(Long id, Tag tag, Page page) {
        this.id = id;
        this.tag = tag;
        this.page = page;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tag getTag() {
        return tag;
    }

    public Page getPage() {
        return page;
    }

}
