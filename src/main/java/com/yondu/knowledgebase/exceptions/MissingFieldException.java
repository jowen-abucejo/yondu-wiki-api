package com.yondu.knowledgebase.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.stream.Collectors;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingFieldException extends RuntimeException{
    public MissingFieldException(String... fields) {
        super("Required fields are not filled yet. " + Arrays.stream(fields).collect(Collectors.joining(",")));
    }
}
