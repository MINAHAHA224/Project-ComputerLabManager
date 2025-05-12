package com.example.computerweb.repositories.custom;

import com.example.computerweb.DTO.dto.semesterResponse.SemesterYearDto;
import com.example.computerweb.DTO.dto.semesterResponse.WeekTimeDto;

import java.util.List;

public interface WeekSemesterRepositoryCustom {

    List<SemesterYearDto> findAllSemesterYear ();

    List<WeekTimeDto> findAllWeekTimeOfSemesterYear(String semesterYear);
}
