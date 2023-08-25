package com.yondu.knowledgebase.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class LoginAttemptException extends RuntimeException{

    public LoginAttemptException(String message) {
        super(message);
    }
}
