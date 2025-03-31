package com.example.computerweb.services;

import com.example.computerweb.DTO.dto.CalendarManagementDto;
import com.example.computerweb.DTO.dto.CalendarResponseDto;
import com.example.computerweb.DTO.dto.CalendarResponseFields;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestDto;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestOneDto;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestRoomDto;
import com.example.computerweb.models.entity.CalendarEntity;
import com.example.computerweb.models.entity.PracticeCaseEntity;
import com.example.computerweb.models.entity.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ICalendarService {
    List<CalendarManagementDto> handleGetAllDataCalendar ();

    CalendarResponseFields handleGetDataForCreatePage ();


    CalendarResponseFields handleGetDataForCreateRoomPage ();
    CalendarResponseDto handleGetDataForUpdatePage (Long calendarId);

    ResponseEntity<String> handleCreateCalendar (CalendarRequestDto calendarRequestDto);
    ResponseEntity<String> handleCreateRoom (CalendarRequestRoomDto calendarRequestRoomDto);

    ResponseEntity<String> handleUpdateCalendar ( CalendarRequestOneDto calendarRequestOneDto);

    void handleDeleteCalendar (String calendarId);

    // check 2 round , first is existPracticeCaseDateUser , second is calendarRoom

    // check 1 round , first is calendarRoom



}
