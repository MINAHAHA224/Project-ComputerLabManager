package com.example.computerweb.services;

import com.example.computerweb.DTO.dto.CalendarManagementDto;
import com.example.computerweb.DTO.dto.CalendarResponseDto;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestDto;
import com.example.computerweb.models.entity.CalendarEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ICalendarService {
    List<CalendarManagementDto> handleGetAllDataCalendar ();

    Map<String, Map<String ,String>> handleGetDataForCreatePage ();
    CalendarResponseDto handleGetDataForUpdatePage (Long calendarId);

    void handleCreateCalendar (CalendarRequestDto calendarRequestDto);

    void handleUpdateCalendar ( CalendarRequestDto calendarRequestDto);

    void handleDeleteCalendar (Long calendarId);

    CalendarEntity handleGetCalendarById ( Long id);


}
