package com.example.computerweb.DTO.dto.semesterResponse;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WeekTimeDto {
    private String idWeekTime;
    private String week;
    private Date timeBegin;
    private Date timeEnd;
}
