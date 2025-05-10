package com.example.computerweb.DTO.dto.calendarResponse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalendarResponseDto {
    private CalendarResponseOneDto userCurrent;

   private CalendarResponseFields dataBase;


}
