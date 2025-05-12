package com.example.computerweb.DTO.requestBody.calendarRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalendarRequestDetailDto {
    @JsonProperty("groupId")
    @Schema(type = "Long", example = "1")
    @NotNull(message = "Group ID không được để trống")
    private Long groupId;

    @JsonProperty("weekSemesterId")
    @Schema(type = "Long", example = "1")
    @NotNull(message = "Week Semester ID không được để trống")
    private Long weekSemesterId;

    @JsonProperty("dayId")
    @Schema(type = "Long", example = "1")
    @NotNull(message = "Day ID không được để trống")
    private Long dayId;

    @JsonProperty("practiceCaseBeginId")
    @Schema(type = "Long", example = "1")
    @NotNull(message = "Practice Case Begin ID không được để trống")
    private Long practiceCaseBeginId;

    @JsonProperty("allCase")
    @Schema(type = "Long", example = "1")
    @NotNull(message = "All Case ID không được để trống")
    private Long allCase;


    @JsonProperty("purposeUse")
    @Schema(type = "String", example = "can be blank")
    @Size(max = 255, message = "Purpose Use không được vượt quá 255 ký tự")
    private String purposeUse;
}
