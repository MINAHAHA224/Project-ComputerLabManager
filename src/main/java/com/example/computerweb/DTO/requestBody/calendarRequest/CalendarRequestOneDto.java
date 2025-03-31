package com.example.computerweb.DTO.requestBody.calendarRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalendarRequestOneDto {

    @JsonProperty("calendarId")
    @Schema(type = "Long",  example = "1")
    private Long calendarId;

    @JsonProperty("weekSemesterId")
    @Schema(type = "Long",  example = "1")
    private Long weekSemesterIdNew;

    @JsonProperty("dayId")
    @Schema(type = "Long",  example = "1")
    private Long dayId;

    @JsonProperty("practiceCaseBeginId")
    @Schema(type = "Long",  example = "1")
    private Long practiceCaseBeginIdNew;

    @JsonProperty("roomId")
    @Schema(type = "Long",  example = "1")
    private Long roomIdNew;

    @JsonProperty("purposeUse")
    @Schema(type = "String",  example = "can be blank")
    private String purposeUseNew;
}
