package com.example.computerweb.DTO;

import com.example.computerweb.Validation.login.LoginChecked;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Data
@LoginChecked
public class UserLoginDTO {


    @JsonProperty("email")
    private String email;


    @JsonProperty("passWord")
    private String passWord;
}
