package com.yondu.knowledgebase.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.yondu.knowledgebase.entities.Page;

public interface PageService {

    public Page findById(Long id);

    public List<Page> findAll();
}
