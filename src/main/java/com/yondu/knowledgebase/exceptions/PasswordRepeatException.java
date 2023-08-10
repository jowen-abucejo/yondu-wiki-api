package com.yondu.knowledgebase.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PasswordRepeatException extends RuntimeException{

    public PasswordRepeatException() {
        super("The password you entered has already been used for your account. To maintain the security of your account, please choose a new password that you haven't used before.");
    }
}
