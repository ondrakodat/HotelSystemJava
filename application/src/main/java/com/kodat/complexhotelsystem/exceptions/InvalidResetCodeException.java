package com.kodat.complexhotelsystem.exceptions;

public class InvalidResetCodeException extends RuntimeException {
    public InvalidResetCodeException(String message) {
        super(message);
    }
}
