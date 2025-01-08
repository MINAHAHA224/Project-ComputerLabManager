package com.example.computerweb.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Data
public class UserLoginDTO {

    @NotBlank(message = "Email không được để trống")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @JsonProperty("passWord")
    private String passWord;
}
