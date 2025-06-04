package com.example.computerweb.DTO.requestBody.calendarRequest;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalendarCheckRoomDto {
    private Long creditClassId;
    private Long idFacility;


    private Long weekSemesterId;
    private Long dayId;
    private Long practiceCaseBeginId;
    private Long allCase;

}
