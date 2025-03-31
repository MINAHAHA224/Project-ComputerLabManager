package com.example.computerweb.DTO.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalendarResponseFields {
    private  ArrayList<Map<String, String>> creditClass;
    private  ArrayList<Map<String, String>> weekSemester;
    private  ArrayList<Map<String, String>> group;
    private  ArrayList<Map<String, String>> day;
    private  ArrayList<Map<String, String>> practiceCase;
    private  ArrayList<Map<String, String>> room;
    private  ArrayList<Map<String, String>> teacher;
  // private Map<String, Map<String, String>> creditClass;
    // data ma  co key thi de  { "key"" : "value" , "key"" : "value"}  ,
    // con data ma ko co key thi [ { key : value} ,{ key : value} ]



}
