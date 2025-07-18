package com.yondu.knowledgebase.exceptions;

import com.yondu.knowledgebase.DTO.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class DefaultExceptionHandler {
    @ExceptionHandler({ResponseStatusException.class, Exception.class, AccessDeniedException.class,NoContentException.class, DuplicateResourceException.class, RequestValidationException.class, ResourceNotFoundException.class, InvalidRatingException.class, InvalidCredentialsException.class, UserException.class, InvalidNotificationTypeException.class, CommentIsNotAllowed.class, PasswordRepeatException.class, InvalidEmailException.class, MissingFieldException.class})
    public ResponseEntity<?> handleException(Exception e,
                                                    HttpServletRequest request) {
        HttpStatus httpStatus;
        if(e instanceof ResponseStatusException) {
            ResponseStatusException rsException = (ResponseStatusException) e;
            httpStatus = HttpStatus.valueOf(rsException.getStatusCode().value());
        }else{
            if (e instanceof AccessDeniedException ||
                e instanceof InvalidCredentialsException ||
                e instanceof CommentIsNotAllowed) {
                httpStatus = HttpStatus.UNAUTHORIZED;
            } else if (e instanceof DuplicateResourceException ||
                       e instanceof InvalidEmailException ||
                       e instanceof PasswordRepeatException
            ) {
                httpStatus = HttpStatus.CONFLICT;
            } else if (e instanceof RequestValidationException ||
                       e instanceof MissingFieldException ||
                       e instanceof UserException ||
                       e instanceof InvalidRatingException ||
                    e instanceof InvalidNotificationTypeException
            ) {
                httpStatus = HttpStatus.BAD_REQUEST;
            } else if (e instanceof ResourceNotFoundException) {
                httpStatus = HttpStatus.NOT_FOUND;
            } else if(e instanceof NoContentException) {
                httpStatus = HttpStatus.NO_CONTENT;
            } else if(e instanceof ResourceDeletionException) {
                httpStatus = HttpStatus.FORBIDDEN;
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        return ResponseEntity.status(httpStatus).body(ApiResponse.error(e.getMessage()));
    }

}