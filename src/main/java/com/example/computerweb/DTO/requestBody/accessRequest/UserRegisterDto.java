package com.example.computerweb.DTO.requestBody.accessRequest;


import com.example.computerweb.Validation.EmailValidation.EmailChecked;
import com.example.computerweb.Validation.EnumPatternValidation.EnumPattern;
import com.example.computerweb.Validation.PasswordValidation.PasswordChecked;
import com.example.computerweb.Validation.PhoneValidation.PhoneChecked;
import com.example.computerweb.models.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.time.LocalDate;


@Getter
@Setter
@ToString
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto implements Serializable {

    @NotBlank(message = "firstName must be not blank")
    @JsonProperty("firstName")
    @Schema(type = "string" , example = "Cao Duy")
    private String firstName;

    @NotBlank(message = "lastName must be not blank")
    @Schema(type = "string" , example = "Thái")
    @JsonProperty("lastName")
    private String lastName;


    @JsonProperty("phone")
    @PhoneChecked(message = "phone invalid format")
    @Schema(type = "string" , example = "0964515599")
    private String phone ;

    //@NotBlank(message = "dateOfBirth must be not blank")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(type = "string", format = "date", example = "2025-02-19")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate  dateOfBirth;


    @JsonProperty("gender")
   @EnumPattern(name = "gender" , regexp = "NAM|NU")
    @Schema(type = "string", example = "NAM|NU")
    private Gender gender;

    @JsonProperty("informationCode")
    @Schema(type = "string", example = "0129310923102")
    private String informationCode;

    @JsonProperty("roleId")
    @Schema(type = "Long", example = "1")
    private Long roleId;


    @JsonProperty("majorId")
    @Schema(type = "Long", example = "1")
    private Long majorId;

}
