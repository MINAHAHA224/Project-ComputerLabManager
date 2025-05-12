package com.example.computerweb.DTO.requestBody.creditClassRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Validated
public class CreditClassRqCreateDto {
    @JsonProperty("idSubject")
    @NotNull(message = "Môn học không được để trống")
    @Schema(type = "Long" , example = "2")
    private Long idSubject;



    @JsonProperty("codeCreditClass")
    @NotBlank(message = "Mã lớp tín chỉ không được để trống")
    @Schema(type = "String" , example = "MIX_E22_CLC")
    private String codeCreditClass;

    @JsonProperty("idClassroom")
    @NotNull(message = "Lớp học không được để trống")
    @Schema(type = "Long" , example = "2")
    private Long idClassroom;

    @JsonProperty("numberOfStudentLTC")
    @NotNull(message = "Số lượng sinh viên của lớp tín chỉ không được để trống")
    @Min(value = 15  , message = "Must > 15 , can open class")
    @Schema(type = "Long" , example = "20")
    private Long numberOfStudentLTC ;


    @JsonProperty("teacherId")
    @NotNull(message = "Giáo viên chưa được chọn")
    @Schema(type = "Long" , example = "7")
    private Long teacherId;


    @JsonProperty("group")
    @NotBlank( message = "Nhóm lớp tín chỉ không được để trống")
//    @Pattern(regexp = "^(01|02|03)$", message = "group must be 01, 02, or 03")
    @Pattern(regexp = "^\\d{2}$", message = "Nhóm phải có chính xác 2 chữ số")
    @Schema(type = "String" , example = "02")
    private String group;

//    @JsonProperty("creditLTC")
//    @NotNull(message = "credit must not be null")
//    @Min(value = 1 , message = "credit must greater than zero")
//    @Schema(type = "String" , example = "4")
//    private Long creditLTC;


}
