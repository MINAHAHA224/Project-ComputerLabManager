package com.example.computerweb.DTO.reponseBody;

public class ResponseSuccess<T> extends ResponseData<T>{
    public ResponseSuccess(int status, String message) {
        super(status, message);
    }

    public ResponseSuccess(int status, String message, T data) {
        super(status, message, data);
    }
}
