package com.nexus.payment.processing.advice;

import com.nexus.payment.processing.dto.Notification;
import com.nexus.payment.processing.exceptions.ClientNotFoundException;
import com.nexus.payment.processing.exceptions.ClientViolatorException;
import com.nexus.payment.processing.exceptions.util.ErrorMapperUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The advice handles error condition and returns response.
 * This helps in achieving separation of concern.
 * Service class should only focus on the business logic and leave the non business logic to AOP's classes.
 * Also for logging we could ues @Aspect
 */
@ControllerAdvice
public class PaymentProcessControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<Object> handleClientNotFoundException(
            ClientNotFoundException ex, WebRequest request) {

        List<Notification> notifications =  ErrorMapperUtil.remapToNotifications(ex.getErrorMetaData());
        return new ResponseEntity<>(notifications, ErrorMapperUtil.getHttpStatus(ex.getErrorMetaData()));
    }

    @ExceptionHandler(ClientViolatorException.class)
    public ResponseEntity<Object> handleClientViolatorException(
            ClientViolatorException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Client is a violator, " + ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
