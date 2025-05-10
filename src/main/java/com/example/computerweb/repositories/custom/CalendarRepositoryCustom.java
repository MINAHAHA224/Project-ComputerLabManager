package com.example.computerweb.repositories.custom;

import com.example.computerweb.DTO.dto.calendarResponse.CalendarManagementDto;

import java.util.List;


public interface CalendarRepositoryCustom {


   List<CalendarManagementDto>  findAllCustom ();
}
