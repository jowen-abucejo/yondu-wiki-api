package com.yondu.knowledgebase.services.implimentations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.entities.Page;
import com.yondu.knowledgebase.repositories.PageRepository;
import com.yondu.knowledgebase.services.PageService;

@Service
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;

    /**
     * @param pageRepository
     */
    public PageServiceImpl(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Override
    public Page findById(Long id) {
        return pageRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Page> findAll() {
        return pageRepository.findAll();
    }

}
