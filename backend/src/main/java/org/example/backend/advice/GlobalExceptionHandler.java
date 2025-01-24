package org.example.backend.advice;

import lombok.extern.log4j.Log4j2;
import org.example.backend.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    private static final String ERROR_MESSAGE_GENERIC = "Something went wrong";
    private static final String ERROR_MESSAGE_NOT_READABLE = "Not readable request body";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHandlerMethodValidationException(HandlerMethodValidationException exception) {
        log.info(exception);

        return new ErrorResponse(exception.getReason());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.info(exception);

        return new ErrorResponse(ERROR_MESSAGE_NOT_READABLE);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoResourceFoundException(NoResourceFoundException exception) {
        log.info(exception);

        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {
        log.info(exception);

        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(NoSuchElementException exception) {
        log.info(exception);

        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.info(exception);

        return new ErrorResponse(
                exception.getBindingResult().getAllErrors().stream().map(
                        (ObjectError fieldError) -> "%s: %s".formatted(
                                ((FieldError) fieldError).getField(),
                                fieldError.getDefaultMessage()
                        )
                ).collect(Collectors.joining(";"))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGlobalException(Exception exception) {
        log.error(exception);

        return new ErrorResponse(ERROR_MESSAGE_GENERIC);
    }
}
