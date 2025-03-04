package com.example.computerweb.DTO.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarManagementDto implements Serializable {

    private String date;
    private String teacher;
    private String room;
    private String subject;
    private String classroom;
    private String practiceCase;
    private String time;
    private String note;
    private String id;

}
