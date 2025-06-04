package com.example.computerweb.DTO.requestBody.calendarRequest;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalendarRequestNoAutoDto {
    @JsonProperty("creditClassId")
    @Schema(type = "Long", example = "1")
    @NotNull(message = "Lớp tín chỉ không được để trống")
    private Long creditClassId;

    @JsonProperty("idFacility")
    @Schema(type = "Long", example = "1")
    @NotNull(message = "Cơ sở không được để trống")
    private Long idFacility;

    @NotEmpty(message = "Danh sách Calendar Detail không được để trống")
    @Valid // Để kích hoạt validate cho các phần tử bên trong List
    List<CalendarRequestDetailDto> calendarDetail;
}
