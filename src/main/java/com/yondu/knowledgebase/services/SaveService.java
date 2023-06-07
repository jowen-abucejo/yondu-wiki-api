package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.save.SaveDTO;
import com.yondu.knowledgebase.DTO.save.SaveDTOMapper;
import com.yondu.knowledgebase.entities.Save;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.repositories.SaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SaveService {

    @Autowired
    private SaveRepository saveRepository;

public SaveDTO.BaseResponse createSaved(SaveDTO.BaseRequest saves) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    Save saved = SaveDTOMapper.mapBaseToEntity(saves);
    saved.setDateCreated(LocalDateTime.now());
    saved.setAuthor(user);


    SaveDTO.BaseResponse saveRes = SaveDTOMapper.mapEntityToBaseResponse(saveRepository.save(saved));
    return saveRes;
}
}
