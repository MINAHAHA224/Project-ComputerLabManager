package com.example.computerweb.DTO.requestBody.calendarRequest;

import com.example.computerweb.models.enums.PurposeUse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
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

    private Long creditClassId;

//Group 1
    @JsonProperty("groupId1")
    @Schema(type = "Long",  example = "1")
    private Long groupId1;

    @JsonProperty("weekSemesterId1")
    @Schema(type = "Long",  example = "1")
    private Long weekSemesterId1;

    @JsonProperty("dayId1")
    @Schema(type = "Long",  example = "1")
    private Long dayId1;

    @JsonProperty("practiceCaseBeginId1")
    @Schema(type = "Long",  example = "1")
    private Long practiceCaseBeginId1;

    @JsonProperty("allCase1")
    @Schema(type = "Long",  example = "1")
    private Long allCase1;

    @JsonProperty("roomId1")
    @Schema(type = "Long",  example = "1")
    private Long roomId1;

    @JsonProperty("purposeUse1")
    @Schema(type = "String",  example = "can be blank")
    private String purposeUse1;


//Group 2
    @JsonProperty("groupId2")
    @Schema(type = "Long",  example = "1")
    private Long groupId2;

    @JsonProperty("weekSemesterId2")
    @Schema(type = "Long",  example = "1")
    private Long weekSemesterId2;

    @JsonProperty("dayId2")
    @Schema(type = "Long",  example = "1")
    private Long dayId2;

    @JsonProperty("practiceCaseBeginId2")
    @Schema(type = "Long",  example = "1")
    private Long practiceCaseBeginId2;

    @JsonProperty("allCase2")
    @Schema(type = "Long",  example = "1")
    private Long allCase2;

    @JsonProperty("roomId2")
    @Schema(type = "Long",  example = "1")
    private Long roomId2;

    @JsonProperty("purposeUse2")
    @Schema(type = "String",  example = "can be blank")
    private String purposeUse2;


}
