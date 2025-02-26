package com.example.computerweb.models.enums;

import java.util.Map;
import java.util.TreeMap;

public enum PurposeUse {
    LICH_CHINH_THUC ( "Official schedule"),
    LICH_THAY_DOI ("Schedule changes"),

    LICH_MUON_PHONG("Room rental schedule"),
    ;

    private final String purposeName;

    PurposeUse( String purposeName){
        this.purposeName = purposeName;
    }

    public static Map<String, String> getPurposeUse (){
        Map<String , String > codes = new TreeMap<>();
        for (PurposeUse key : PurposeUse.values() ){
            codes.put(key.toString() ,key.purposeName );
        }

        return codes;
    }

}
