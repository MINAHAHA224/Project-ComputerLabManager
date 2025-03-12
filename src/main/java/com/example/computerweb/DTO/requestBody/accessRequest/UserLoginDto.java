package com.example.computerweb.DTO.requestBody.accessRequest;

import com.example.computerweb.Validation.EmailValidation.EmailChecked;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Builder
@ToString
@Validated
public class UserLoginDto implements Serializable {



    @JsonProperty("email")
    @Schema(type = "string" , example = "ct@ptithcm.edu.vn")
    @EmailChecked
    private String email;



    @JsonProperty("passWord")
    @Schema(type = "string" , example = "Abc123")
    @NotBlank(message = "Password must not be blank")
    private String passWord;

    public String getEmail() {
        return email;
    }

    public String getPassWord() {
        return passWord;
    }
}
