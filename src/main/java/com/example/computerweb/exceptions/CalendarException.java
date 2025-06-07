package com.example.computerweb.exceptions;

import lombok.Getter;

@Getter
public class CalendarException extends  RuntimeException{
    private final String errorMessage;

    public CalendarException(String message) {
        super(message);
        this.errorMessage = message;
    }

}
