package com.example.computerweb.exceptions;

import lombok.Getter;

@Getter
public class DataConflictException extends  RuntimeException{
    private final String errorMessage;

    public DataConflictException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
