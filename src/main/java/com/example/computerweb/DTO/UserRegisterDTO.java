package com.example.computerweb.DTO;


import com.example.computerweb.Validation.register.RegisterChecked;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RegisterChecked
public class UserRegisterDTO {

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("passWord")
    private String passWord;

    @JsonProperty("retypePassword")
    private String retypePassword;

    @JsonProperty("dateOfBirth")
    private String dateOfBirth ;

    @JsonProperty("role")
    private Long role;
}
