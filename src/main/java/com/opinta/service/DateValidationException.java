package com.opinta.service;

public class DateValidationException extends Exception {
    public DateValidationException() {
        super();
    }

    public DateValidationException(String message) {
        super(message);
    }
}
