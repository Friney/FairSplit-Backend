package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.core.entity.api_exception.ApiException;
import com.friney.fairsplit.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiException> handleServiceException(ServiceException e) {
        return new ResponseEntity<>(new ApiException(e.getMessage()), e.getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiException> handle(RuntimeException exception) {
        log.info("{} -> {}", exception.getClass(), exception.getMessage());
        return new ResponseEntity<>(new ApiException(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
