package com.yondu.knowledgebase.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidEmailException extends RuntimeException{
    public InvalidEmailException() {
        super("Email is not valid. The email must be from Yondu only.");
    }
}
