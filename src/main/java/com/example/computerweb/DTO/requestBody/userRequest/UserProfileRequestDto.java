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
    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(regexp = "\\d{6,}", message = "Mật khẩu phải có ít nhất 6 chữ số")
    private String resetPassword;

    @JsonProperty("phone")
    @PhoneChecked(message = "Số điện thoại không đúng định dạng")
    @Schema(type = "string" , example = "0964515599")
    private String phone ;

    @JsonProperty("informationCode")
    @NotBlank(message = "Mã thông tin không được để trống")
    @Schema(type = "string" , example = "01239129310231")
    private String informationCode;

    @JsonProperty("emailPersonal")
    @NotBlank(message = "Email cá nhân không được để trống")
    @Schema(type = "string" , example = "abc@Gmail.com")
    private String emailPersonal;

    @JsonProperty("province")
    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Schema(type = "string" , example = "Tỉnh Vũng Tàu")
    private String province;


    @JsonProperty("district")
    @NotBlank(message = "Quận/Huyện không được để trống")
    @Schema(type = "string" , example = "TP Bà Rịa")
    private String district;


    @JsonProperty("ward")
    @NotBlank(message = "Xã/Phường không được để trống")
    @Schema(type = "string" , example = "Phường 12")
    private String ward;


    @JsonProperty("address")
    @NotBlank(message = "Địa chỉ không được để trống")
    @Schema(type = "string" , example = "100 Nguyễn Văn Cừ")
    private String address;

}
