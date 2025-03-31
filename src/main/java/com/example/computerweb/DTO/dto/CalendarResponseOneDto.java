package com.example.computerweb.DTO.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CalendarResponseOneDto {
    private String calendarId;
    private String creditClassId;
    private String userIdMp_Fk;

    private String groupId;


    private String weekSemesterId;


    private String dayId;


    private String practiceCaseBeginId;


    private String allCase;


    private String roomId;

    private String purposeUse;
}
