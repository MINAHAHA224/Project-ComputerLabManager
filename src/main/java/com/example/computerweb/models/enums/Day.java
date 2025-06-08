package com.example.computerweb.models.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Getter
public enum Day {
    MONDAY(2, "Thứ 2"),
    TUESDAY(3, "Thứ 3"),
    WEDNESDAY(4, "Thứ 4"),
    THURSDAY(5, "Thứ 5"),
    FRIDAY(6, "Thứ 6"),
    SATURDAY(7, "Thứ 7"),
    SUNDAY(8, "Chủ nhật");

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
