package com.example.computerweb.models.enums;

import java.util.Map;
import java.util.TreeMap;

public enum Major {
    CNTT("Công Nghệ Thông Tin"),
    KTPM("Kĩ Thuật Phần Mềm"),
    TTNT("Trí Tuệ Nhân Tạo"),
    ;

    private final String majorName;

    Major( String majorName){
        this.majorName = majorName;
    }

    public static Map<String, String> getMajorUse (){
        Map<String , String > codes = new TreeMap<>();
        for (Major key : Major.values() ){
            codes.put(key.toString() ,key.majorName );
        }

        return codes;
    }
}
