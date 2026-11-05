package com.kodat.complexhotelsystem.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;

public class EmailIsNotValidException extends RuntimeException {
    public EmailIsNotValidException(String message) {
        super(message);
    }
}
