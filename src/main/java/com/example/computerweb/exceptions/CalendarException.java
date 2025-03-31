package com.example.computerweb.exceptions;

public class CalendarException extends  RuntimeException{
    private final String errorMessage;

    public CalendarException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
