package com.example.computerweb.DTO.requestBody.calendarRequest;

import com.example.computerweb.models.enums.PurposeUse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Validated
public class CalendarRequestDto {
    @JsonProperty("creditClassId")
    @Schema(type = "Long",  example = "1")
    @NotNull(message = "Lớp tín chỉ không được để trống")
    private Long creditClassId;

    @JsonProperty("idFacility")
    @Schema(type = "Long",  example = "1")
    @NotNull(message = "Cơ sở không được để trống")
    private Long idFacility;

    @NotEmpty(message = "Danh sách Calendar Detail không được để trống")
    @Valid // Để kích hoạt validate cho các phần tử bên trong List
    List<CalendarRequestDetailDto> calendarDetail;

}
