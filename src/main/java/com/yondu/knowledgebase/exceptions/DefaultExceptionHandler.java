package com.yondu.knowledgebase.exceptions;

import com.yondu.knowledgebase.DTO.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class DefaultExceptionHandler {
    @ExceptionHandler({AccessDeniedException.class, DuplicateResourceException.class, RequestValidationException.class, ResourceNotFoundException.class})
    public ResponseEntity<?> handleException(Exception e,
                                                    HttpServletRequest request) {
        HttpStatus httpStatus;
        if (e instanceof AccessDeniedException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (e instanceof DuplicateResourceException ||
                    e instanceof InvalidEmailException
        ) {
            httpStatus = HttpStatus.CONFLICT;
        } else if (e instanceof RequestValidationException ||
                e instanceof MissingFieldException ||
                e instanceof UserException
        ) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (e instanceof ResourceNotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if(e instanceof NoContentException) {
            httpStatus = HttpStatus.NO_CONTENT;
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(httpStatus).body(ApiResponse.error(e.getMessage()));
    }

}