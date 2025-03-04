package com.example.computerweb.DTO.dto;

import lombok.*;

import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalendarResponseFields {

    private Map<String , Map<String ,String>> field;

    private Map<String , Map<String ,Map<String,String>>> fields;
}
