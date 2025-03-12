package com.example.computerweb.services;

import com.example.computerweb.DTO.dto.CalendarManagementDto;
import com.example.computerweb.DTO.dto.CalendarResponseDto;
import com.example.computerweb.DTO.dto.CalendarResponseFields;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestDto;
import com.example.computerweb.models.entity.CalendarEntity;
import com.example.computerweb.models.entity.PracticeCaseEntity;
import com.example.computerweb.models.entity.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public interface ICalendarService {
    List<CalendarManagementDto> handleGetAllDataCalendar ();

    CalendarResponseFields handleGetDataForCreatePage ();
    CalendarResponseDto handleGetDataForUpdatePage (Long calendarId);

    ResponseEntity<String> handleCreateCalendar (CalendarRequestDto calendarRequestDto);

    ResponseEntity<String> handleUpdateCalendar ( CalendarRequestDto calendarRequestDto);

    void handleDeleteCalendar (Long calendarId);

    // check 2 round , first is existPracticeCaseDateUser , second is calendarRoom
    ResponseEntity<String> handleCheckExistCalendar2E (Date date , PracticeCaseEntity practiceCase , UserEntity user , List<Long> idRoom );

    // check 1 round , first is calendarRoom
    ResponseEntity<String> handleCheckExistCalendar1E ( Date date ,PracticeCaseEntity practiceCase , List<Long> idRoom );
    CalendarEntity handleGetCalendarById ( Long id);


}
