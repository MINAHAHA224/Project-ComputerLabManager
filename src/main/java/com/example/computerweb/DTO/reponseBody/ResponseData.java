package com.example.computerweb.DTO.reponseBody;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

public class ResponseData<T> {
    @Schema(type = "int" , example = "200")
    private final int status;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;


    // PUT , PATCH , DELETE
    public ResponseData(int status, String message) {
        this.status = status;
        this.message = message;
    }

    // GET
    public ResponseData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }


}
