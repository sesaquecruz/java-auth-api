package org.auth.api.infrastructure.api.controllers;

import org.auth.api.domain.exceptions.InternalErrorException;
import org.auth.api.domain.exceptions.NotificationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = NotificationException.class)
    public ResponseEntity<?> notificationException(final NotificationException ex) {
        return ResponseEntity
                .unprocessableEntity()
                .body(ex.getErrors());
    }

    @ExceptionHandler(value = InternalErrorException.class)
    public ResponseEntity<?> internalErrorException(final InternalErrorException ex) {
        if (ex.getCause() != null)
            ex.getCause().printStackTrace();
        return ResponseEntity
                .internalServerError()
                .body(ex.getMessage());
    }
}
