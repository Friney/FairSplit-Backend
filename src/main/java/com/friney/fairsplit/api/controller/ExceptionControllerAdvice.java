package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.core.entity.api_exception.ApiException;
import com.friney.fairsplit.core.exception.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiException> handleServiceException(ServiceException e) {
        return new ResponseEntity<>(new ApiException(e.getMessage()), e.getHttpStatus());
    }
}
