package org.example.backend.advice;

import lombok.extern.log4j.Log4j2;
import org.example.backend.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    private static final String GENERIC_ERROR_MESSAGE = "Something went wrong";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.info(exception);
        return new ErrorResponse(exception.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGlobalException(Exception exception) {
        log.error(exception);
        return new ErrorResponse(GENERIC_ERROR_MESSAGE);
    }
}
