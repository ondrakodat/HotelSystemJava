package com.kodat.complexhotelsystem.exception;

import com.kodat.complexhotelsystem.exceptions.EmailIsNotValidException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Central place for exception handling for controllers.
 * We use MethodArgumentNotValidException.class to catch specific Exception and not all of them.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailIsNotValidException.class)
    public void catchEmailException(EmailIsNotValidException e){

    }



}
