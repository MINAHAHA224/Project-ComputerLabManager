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

    private String calendarId;
    private String creditClassId;
    private String credit;
    private String UserIdMp_FK;
    private String codeSubject;
    private String nameSubject;
    private String group;
    private String combination;
    private String codeClassroom;
    private String nameRoom;
    private String codeFacility;
    private String day;
    private String lesson;
    private String lessonBegin;
    private String nameTeacher;
    private String note;
    private String date;
    private String statusCalendar;



}
