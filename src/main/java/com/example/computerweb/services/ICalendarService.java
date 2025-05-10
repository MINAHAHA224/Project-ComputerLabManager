package com.example.computerweb.services;

import com.example.computerweb.DTO.dto.calendarResponse.CalendarManagementDto;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseDto;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseFields;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestDto;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestOneDto;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestRoomDto;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ICalendarService {
    List<CalendarManagementDto> handleGetAllDataCalendar ();

    CalendarResponseFields handleGetDataForCreatePage ();

    ArrayList<Map<String, String>> handleWeekStudyForCreateCreditClass ( Long codeCreditClass);


    CalendarResponseFields handleGetDataForCreateRoomPage ();
    CalendarResponseDto handleGetDataForUpdatePage (Long calendarId);

    ResponseEntity<String> handleCreateCalendar (CalendarRequestDto calendarRequestDto);
    ResponseEntity<String> handleCreateRoom (CalendarRequestRoomDto calendarRequestRoomDto);

    ResponseEntity<String> handleUpdateCalendar ( CalendarRequestOneDto calendarRequestOneDto);

    void handleDeleteCalendar (String calendarId);

    // check 2 round , first is existPracticeCaseDateUser , second is calendarRoom

    // check 1 round , first is calendarRoom



}
