package com.example.computerweb.DTO.requestBody.calendarRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalendarRequestRoomDto {
    @JsonProperty("userIdMp_FK")
    @Schema(type = "Long",  example = "1")
    private Long userIdMp_FK;

    @JsonProperty("weekSemesterId")
    @Schema(type = "Long",  example = "1")
    private Long weekSemesterId;

    @JsonProperty("dayId")
    @Schema(type = "Long",  example = "1")
    private Long dayId;

    @JsonProperty("practiceCaseBeginId")
    @Schema(type = "Long",  example = "1")
    private Long practiceCaseBeginId;

    @JsonProperty("allCase")
    @Schema(type = "Long",  example = "4")
    private Long allCase;

    @JsonProperty("roomId")
    @Schema(type = "Long",  example = "1")
    private Long roomId;

    @JsonProperty("purposeUse")
    @Schema(type = "String",  example = "can be blank")
    private String purposeUse;
}
