package com.yondu.knowledgebase.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RuntimeException{

    public InvalidCredentialsException() {
        super("Invalid Credentials.");
    }
}
