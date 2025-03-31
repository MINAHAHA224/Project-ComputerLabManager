package com.example.computerweb.models.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Getter
public enum Day {
    MONDAY(2, "Monday"),
    TUESDAY(3, "Tuesday"),
    WEDNESDAY(4, "Wednesday"),
    THURSDAY(5, "Thursday"),
    FRIDAY(6, "Friday"),
    SATURDAY(7, "Saturday"),
    SUNDAY(8, "Sunday");

    private final int number;
    private final String displayName;

    Day(int number , String displayName){
        this.number = number;
        this.displayName = displayName;
    }



    public static Map<String, String> getDay (){
        Map<String , String> data = new TreeMap<>();
        for ( Day key : Day.values()){
            data.put(String.valueOf(key.getNumber()) , key.getDisplayName());
        }

        return data;
    }

}
