package com.example.computerweb.DTO.requestBody.userRequest;

import com.example.computerweb.Validation.EmailValidation.EmailChecked;
import com.example.computerweb.Validation.EnumPatternValidation.EnumPattern;
import com.example.computerweb.models.enums.Gender;
import com.example.computerweb.models.enums.Major;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Validated
public class UserMngRequestDto {

    @JsonProperty("id")
    @Schema(type = "Long" , example = "1")
    private Long id ;

    @JsonProperty("firstName")
    @NotBlank(message = "FirstName must not be blank")
    @Schema(type = "string" , example = "Cao Duy")
    private String firstName;

    @JsonProperty("lastName")
    @NotBlank(message = "lastName must not be blank")
    @Schema(type = "string" , example = "Thái")
    private String lastName;


    @JsonProperty("gender")
    @Schema(type = "string" , example = "NAM|NU")
    @EnumPattern(name = "gender" , regexp = "NAM|NU")
    private Gender gender;

    @JsonProperty("dateOfBirth")
    @Schema(type = "string", format = "date", example = "2025-02-19")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth ;

    @JsonProperty("phone")
    @NotBlank(message = "Phone must not be blank")
    @Schema(type = "string" , example = "0964515577")
    private String phone ;

    @JsonProperty("email")
    @Schema(type = "string" , example = "ct@ptithcm.edu.vn")
    @EmailChecked
    private String email;

    @JsonProperty("informationCode")
    @Schema(type = "string" , example = "918273483427")
    @NotBlank(message = "InformationCode must not be blank")
    private String informationCode;

    @JsonProperty("major")
    @EnumPattern(name = "major" , regexp = "CNTT|KTPM|CTNT")
    @Schema(type = "string" , example = "CNTT|KTPM|CTNT")
    private Major major;

    @JsonProperty("address")
    @NotBlank(message = "Address must not be blank")
    @Schema(type = "string" , example = "19 Nguyen Van Cu")
    private String address;

    @JsonProperty("emailPersonal")
    @NotBlank(message = "EmailPersonal must not be blank")
    @Pattern(message = "EmailPersonal must have standard format" ,
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
    @Schema(type = "string" , example = "minahaha@Gmail.com")
    private String emailPersonal;

    @JsonProperty("Province")
    @Schema(type = "string" , example = "TP.Vung Tau")
    @NotBlank(message = "Province must not be blank")
    private String province;

    @JsonProperty("District")
    @Schema(type = "string" , example = "Phuong Phuoc Hai")
    @NotBlank(message = "District must not be blank")
    private String district;

    @JsonProperty("Ward")
    @Schema(type = "string" , example = "Xa vv")
    @NotBlank(message = "Ward must not be blank")
    private String ward;

    @Column(name = "AnhDD")
    private String avatar;

}
