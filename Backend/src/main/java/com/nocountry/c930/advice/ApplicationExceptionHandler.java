package com.nocountry.c930.advice;

import com.nocountry.c930.exception.NotAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Map<String, String> handleInvalidArgument(BindException ex){

        Map<String,String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error-> errorMap.put(error.getField(), error.getDefaultMessage()));
        return errorMap;

    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotAllowed.class)
    public Map<String,String> notAllowedException(NotAllowed ex){

        Map<String,String> errorMap = new HashMap<>();
        errorMap.put("Unauthorized",ex.getMessage());
        return errorMap;

    }
}
