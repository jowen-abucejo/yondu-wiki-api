package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.entities.PageVersion;
import org.springframework.stereotype.Service;

@Service
public interface ChatbaseService {

    public void updateChatbot(PageVersion pageVersion);
}
