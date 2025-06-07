package com.example.computerweb.exceptions;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException{
    private final String errorMessage;

    public AuthenticationException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
