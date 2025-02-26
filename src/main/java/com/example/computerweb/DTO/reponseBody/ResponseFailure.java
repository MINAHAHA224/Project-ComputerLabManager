package com.example.computerweb.DTO.reponseBody;

public class ResponseFailure extends ResponseData{
    public ResponseFailure(int status, String message) {
        super(status, message);
    }
}
