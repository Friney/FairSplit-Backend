package com.friney.fairsplit.api.controller;

import com.friney.fairsplit.core.entity.api_exception.ApiException;
import com.friney.fairsplit.core.exception.ServiceException;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExceptionControllerAdviceTest {

    @InjectMocks
    private ExceptionControllerAdvice advice;

    @Test
    void testHandleServiceException() {
        String message = "Test error message";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ServiceException exception = new ServiceException(message, status);

        ResponseEntity<ApiException> response = advice.handleServiceException(exception);

        assertEquals(status, response.getStatusCode());
        assertEquals(message, Objects.requireNonNull(response.getBody()).message());
    }
}
