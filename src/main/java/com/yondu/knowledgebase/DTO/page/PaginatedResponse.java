package com.yondu.knowledgebase.DTO.page;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaginatedResponse<T> {
    @JsonProperty(index = 1)
    private List<T> data;

    @JsonProperty(index = 2)
    private Integer page;

    @JsonProperty(index = 3)
    private Integer size;

    @JsonProperty(index = 4)
    private Long total;

    /**
     * @param size
     */
    public PaginatedResponse(List<T> data, Integer page, Integer size, Long total) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    /**
     * @return the data
     */
    public List<T> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<T> data) {
        this.data = data;
    }

    /**
     * @return the page
     */
    public Integer getPage() {
        return this.page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * @return the size
     */
    public Integer getSize() {
        return this.size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @return the total
     */
    public Long getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(Long total) {
        this.total = total;
    }

}
