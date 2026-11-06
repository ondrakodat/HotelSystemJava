package com.kodat.complexhotelsystem.exception;

import com.kodat.complexhotelsystem.exceptions.EmailAlreadyExistsException;
import com.kodat.complexhotelsystem.exceptions.EmailIsNotValidException;
import com.kodat.complexhotelsystem.exceptions.InvalidPasswordException;
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

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public void catchEmailAlreadyExistsException(EmailAlreadyExistsException e){

    }

    @ExceptionHandler(InvalidPasswordException.class)
    public void catchInvalidPasswordException(InvalidPasswordException e){

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void catchArgumentNotValidException(MethodArgumentNotValidException e){

    }




}
