package com.example.computerweb.DTO.requestBody.accessRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Builder
@ToString
@Validated
public class UserLoginDto implements Serializable {



    @JsonProperty("email")
    @Schema(type = "string" , example = "ct@ptithcm.edu.vn")

    private String email;



    @JsonProperty("passWord")
    @Schema(type = "string" , example = "Abc123")
    private String passWord;

    public String getEmail() {
        return email;
    }

    public String getPassWord() {
        return passWord;
    }
}
