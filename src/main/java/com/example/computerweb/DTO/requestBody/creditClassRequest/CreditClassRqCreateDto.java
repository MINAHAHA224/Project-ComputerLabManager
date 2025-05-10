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
    @NotNull(message = "Subject must not be null")
    @Schema(type = "Long" , example = "2")
    private Long idSubject;



    @JsonProperty("codeCreditClass")
    @NotBlank(message = "codeCreditClass must be not blank")
    @Schema(type = "String" , example = "MIX_E22_CLC")
    private String codeCreditClass;

    @JsonProperty("idClassroom")
    @NotNull(message = "Classroom must not be null")
    @Schema(type = "Long" , example = "2")
    private Long idClassroom;

    @JsonProperty("numberOfStudentLTC")
    @NotNull(message = "numberOfStudentLTC must not be null")
    @Min(value = 15  , message = "Must > 15 , can open class")
    @Schema(type = "Long" , example = "20")
    private Long numberOfStudentLTC ;


    @JsonProperty("teacherId")
    @NotNull(message = "teacher must not be null")
    @Schema(type = "Long" , example = "7")
    private Long teacherId;


    @JsonProperty("group")
    @NotBlank( message = "group must not be blank")
//    @Pattern(regexp = "^(01|02|03)$", message = "group must be 01, 02, or 03")
    @Pattern(regexp = "^\\d{2}$", message = "group must be exactly 2 digits")
    @Schema(type = "String" , example = "02")
    private String group;

//    @JsonProperty("creditLTC")
//    @NotNull(message = "credit must not be null")
//    @Min(value = 1 , message = "credit must greater than zero")
//    @Schema(type = "String" , example = "4")
//    private Long creditLTC;


}
