package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.DTO.page.PaginatedResponse;

public interface PageService {

    public PageDTO findById(Long id);

    public PaginatedResponse<PageDTO> findAll(String searchKey, Integer pageNumber, Integer pageSize, String[] sortBy);
}
