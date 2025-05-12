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
public class CreditClassRqUpdateDto {
    @Schema(type = "Long" , example = "3")
    private Long creditClassId;

    @JsonProperty("numberOfStudentLTC")
    @NotNull(message = "Số lượng sinh viên lớp tín chỉ không được để trống")
    @Min(value = 15  , message = "Số lượng sinh viên > 15 , mới có thể mở lớp")
    @Schema(type = "Long" , example = "20")
    private Long numberOfStudentLTC ;

    @JsonProperty("teacherId")
    @NotNull(message = "Giáo viên chưa được chọn")
    @Schema(type = "Long" , example = "7")
    private Long teacherId;

    @JsonProperty("group")
    @NotBlank( message = "Nhóm không được để trống")
//    @Pattern(regexp = "^(01|02|03)$", message = "group must be 01, 02, or 03")
    @Pattern(regexp = "^\\d{2}$", message = "Nhóm phải có chính xác 2 chữ số")
    @Schema(type = "String" , example = "02")
    private String group;
}
