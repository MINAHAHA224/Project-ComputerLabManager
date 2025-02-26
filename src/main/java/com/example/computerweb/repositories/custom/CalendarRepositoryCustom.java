package com.example.computerweb.repositories.custom;

import com.example.computerweb.DTO.dto.CalendarManagementDto;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarRepositoryCustom {


   List<CalendarManagementDto>  findAllCustom ();
}
