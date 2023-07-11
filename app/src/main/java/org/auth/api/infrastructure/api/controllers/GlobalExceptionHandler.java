package org.auth.api.infrastructure.api.controllers;

import org.auth.api.domain.exceptions.notification.IdentifierException;
import org.auth.api.domain.exceptions.GatewayException;
import org.auth.api.domain.exceptions.notification.NotFoundException;
import org.auth.api.domain.exceptions.notification.NotificationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = IdentifierException.class)
    public ResponseEntity<?> identifierException(final IdentifierException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getNotification().getNotifications());
    }

    @ExceptionHandler(value = NotificationException.class)
    public ResponseEntity<?> notificationException(final NotificationException ex) {
        return ResponseEntity
                .unprocessableEntity()
                .body(ex.getNotification().getNotifications());
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<?> notFoundException(final NotFoundException ex) {
        return ResponseEntity
                .status(NOT_FOUND)
                .body(ex.getNotification().getNotifications());
    }

    @ExceptionHandler(value = GatewayException.class)
    public ResponseEntity<?> internalErrorException(final GatewayException ex) {
        if (ex.getCause() != null)
            ex.getCause().printStackTrace();
        return ResponseEntity
                .internalServerError()
                .body(ex.getMessage());
    }
}
