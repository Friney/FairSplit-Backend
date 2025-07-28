package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.api.dto.exception.ApiException;
import com.friney.fairsplit.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiException> handleServiceException(ServiceException e) {
        return new ResponseEntity<>(new ApiException(e.getMessage()), e.getHttpStatus());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiException> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        return new ResponseEntity<>(new ApiException(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiException> handleInternalAuthenticationServiceException(BadCredentialsException e) {
        return new ResponseEntity<>(new ApiException(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiException> handleRuntimeException(RuntimeException exception) {
        log.error("{} -> {}", exception.getClass(), exception.getMessage());
        return new ResponseEntity<>(new ApiException(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
