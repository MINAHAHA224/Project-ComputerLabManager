package com.example.computerweb.exceptions;

import lombok.Getter;

@Getter
public class DataNotFoundException extends RuntimeException{
    private final String errorMessage;


    public DataNotFoundException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
