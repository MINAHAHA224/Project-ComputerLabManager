package com.example.computerweb.models.enums;

import lombok.Getter;

import java.util.Map;
import java.util.TreeMap;

@Getter
public enum PracticeCase {
    TIET1( 1 , "Tiết 1"),
    TIET2( 2 , "Tiết 2"),
    TIET3( 3 , "Tiết 3"),
    TIET4( 4 , "Tiết 4"),
    TIET5( 5 , "Tiết 5"),
    TIET6( 6 , "Tiết 6"),
    TIET7( 7 , "Tiết 7"),
    TIET8( 8 , "Tiết 8"),
    TIET9( 9 , "Tiết 9"),
    TIET10(10 , "Tiết 10"),
    TIET11( 11 , "Tiết 11"),
    TIET12( 12 , "Tiết 12"),
    TIET13( 13 , "Tiết 13"),
    TIET14( 14 , "Tiết 14");

    private final  int number;

    private final String displayName;


    PracticeCase(int number ,String displayName ){
        this.number = number;
        this.displayName = displayName;
    }

    public static Map<String, String> getPracticeCaseE (){
        Map<String , String> data = new TreeMap<>();
        for ( PracticeCase key : PracticeCase.values()){
            data.put(String.valueOf(key.getNumber()) , key.getDisplayName());
        }

        return data;
    }

}
