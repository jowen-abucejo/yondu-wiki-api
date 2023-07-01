package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.entities.PasswordChanges;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.repositories.PasswordChangesRepository;
import com.yondu.knowledgebase.services.PasswordChangesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordChangesServiceImpl implements PasswordChangesService {

    @Autowired
    private PasswordChangesRepository repository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Logger log = LoggerFactory.getLogger(PasswordChangesServiceImpl.class);

    @Override
    public boolean isPasswordExist(User user, String newPassword) {
        log.info("PasswordChangesServiceImpl.checkPassword()");

        List<PasswordChanges> changes = repository.findByUser(user);
        for(int i = 0; i<changes.size(); i++){
            PasswordChanges pc = changes.get(i);
            if(passwordEncoder.matches(newPassword, pc.getPassword()))
                return true;
        }

        return false;
    }

    @Override
    public void saveNewPassword(User user, String newPassword) {
        log.info("PasswordChangesServiceImpl.saveNewPassword()");

        newPassword = passwordEncoder.encode(newPassword);

        PasswordChanges newPassChange = new PasswordChanges(user, newPassword);
        repository.save(newPassChange);
    }
}
