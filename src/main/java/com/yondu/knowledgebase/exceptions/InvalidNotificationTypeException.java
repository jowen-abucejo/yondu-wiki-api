package com.yondu.knowledgebase.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidNotificationTypeException extends RuntimeException{
    public InvalidNotificationTypeException(String message) {
        super(message);
    }
}
