package com.kodat.complexhotelsystem.exceptions;

public class GoogleEmailIsNotVerified extends RuntimeException {
    public GoogleEmailIsNotVerified(String message) {
        super(message);
    }
}
