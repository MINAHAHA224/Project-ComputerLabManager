package com.example.computerweb.customexceptions;

public class InvalidParamException extends Exception{
    public InvalidParamException(String message) {
        super(message);
    }
}
