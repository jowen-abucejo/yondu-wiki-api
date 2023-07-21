package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.page.PageDTO;
import com.yondu.knowledgebase.entities.PageVersion;
import com.yondu.knowledgebase.entities.Post;
import org.springframework.stereotype.Service;

@Service
public interface ChatbaseService {

    public void updateChatbot(PageVersion pageVersion);

    public void updateChatbot(Post post);
}
