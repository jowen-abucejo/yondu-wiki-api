package com.yondu.knowledgebase.services;

import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {

    public String uploadImage(MultipartFile file);

    public boolean deleteAttachment(String imageUrl);

}
