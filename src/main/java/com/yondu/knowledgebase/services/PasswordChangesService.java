package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface PasswordChangesService {

    public boolean isPasswordExist(User user, String newPassword);
    public void saveNewPassword(User user, String newPassword);
}
