package com.yondu.knowledgebase.DTO.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ImageUploadDTO {
    @JsonInclude(Include.NON_EMPTY)
    public String url;

    /**
     * @param url
     */
    public ImageUploadDTO(String url) {
        this.url = url;
    }

}
