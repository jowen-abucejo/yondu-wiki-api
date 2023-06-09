package com.yondu.knowledgebase.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class CommentIsNotAllowed extends RuntimeException{
    public CommentIsNotAllowed(String message) {
        super(message);
    }
}
