package com.example.computerweb.DTO.requestBody.accessRequest;

import com.example.computerweb.Validation.EmailValidation.EmailChecked;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Validated
public class UserLoginDto implements Serializable {



    @JsonProperty("email")
    @Schema(type = "string" , example = "ct@ptithcm.edu.vn")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@ptithcm\\.edu\\.vn$", message = "Email phải có định dạng @ptithcm.edu.vn")
    @NotBlank(message = "Email không được để trống")
    private String email;



    @JsonProperty("passWord")
    @Schema(type = "string" , example = "Abc123")
    @NotBlank(message = "Mật khẩu không được để trống")
    private String passWord;

    public String getEmail() {
        return email;
    }

    public String getPassWord() {
        return passWord;
    }
}
