package com.example.computerweb.DTO.requestBody.userRequest;

import com.example.computerweb.Validation.PhoneValidation.PhoneChecked;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class UserProfileRequestDto {
    @Schema(type = "Long" , example = "1")
    @JsonProperty("id")
    private Long id;

    @JsonProperty("reset password")
    @Schema(type = "string" , example = "0964515599")
    @NotBlank(message = "Password must not be blank")
    @Pattern(regexp = "\\d{6,}", message = "Password must be at least 6 digits")
    private String resetPassword;

    @JsonProperty("phone")
    @PhoneChecked(message = "phone invalid format")
    @Schema(type = "string" , example = "0964515599")
    private String phone ;

    @JsonProperty("informationCode")
    @NotBlank(message = "InformationCode must not be blank")
    @Schema(type = "string" , example = "01239129310231")
    private String informationCode;

    @JsonProperty("emailPersonal")
    @NotBlank(message = "Email personal must not be blank")
    @Schema(type = "string" , example = "abc@Gmail.com")
    private String emailPersonal;

    @JsonProperty("province")
    @NotBlank(message = "Province must not be blank")
    @Schema(type = "string" , example = "Tinh Vung Tau")
    private String province;


    @JsonProperty("district")
    @NotBlank(message = "District must not be blank")
    @Schema(type = "string" , example = "TP Ba Ria")
    private String district;


    @JsonProperty("ward")
    @NotBlank(message = "Ward must not be blank")
    @Schema(type = "string" , example = "Phuong 12")
    private String ward;


    @JsonProperty("address")
    @NotBlank(message = "Address must not be blank")
    @Schema(type = "string" , example = "100 Nguyen Van Cu")
    private String address;

}
