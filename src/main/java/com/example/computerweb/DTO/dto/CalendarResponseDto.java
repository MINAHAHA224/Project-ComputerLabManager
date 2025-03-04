package com.example.computerweb.DTO.dto;

import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestDto;
import com.example.computerweb.models.entity.CalendarEntity;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalendarResponseDto {
    private CalendarManagementDto userCurrent;

   private CalendarResponseFields dataBase;


}
