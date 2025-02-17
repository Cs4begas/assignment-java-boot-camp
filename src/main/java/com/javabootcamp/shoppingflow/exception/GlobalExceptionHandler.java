package com.javabootcamp.shoppingflow.exception;

import com.javabootcamp.shoppingflow.model.exception.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(ValidationException e) {
        return new ApiError(e.getMessage());
    }
}
