package com.example.computerweb.models.enums;

import lombok.Getter;

import java.util.Map;
import java.util.TreeMap;

@Getter
public enum Group {
    NHOM1 ( 1 , "Nhóm 01"),
    NHOM2 ( 2 , "Nhóm 02");

    private final int number;
    private final String displayName;

    Group(int number , String displayName){
        this.number = number;
        this.displayName = displayName;
    }



    public static Map<String, String> getGroup (){
        Map<String , String> data = new TreeMap<>();
        for ( Group key : Group.values()){
            data.put(String.valueOf(key.getNumber()) , key.getDisplayName());
        }

        return data;
    }
}
