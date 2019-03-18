package com.oracle.techtask.controller.error;

import com.oracle.techtask.exception.BadPathException;
import com.oracle.techtask.exception.SizeUndefined;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ResponseExceptionHandler.class);

    @ExceptionHandler(value =  BadPathException.class)
    protected ResponseEntity<Object> handleConflictBadPath(
            RuntimeException ex, WebRequest request) {

        logger.warn("Request was wrong {}", ex);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(value =  SizeUndefined.class)
    protected ResponseEntity<Object> handleConflictSizeUndefined(
            RuntimeException ex, WebRequest request) {

        logger.warn("Request was wrong {}", ex);
        return ResponseEntity.notFound().build();
    }
}
