package com.example.computerweb.DTO.dto.creditClassResponse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreditClassRpPageIndexDto {
    private Long creditClassId;
    private String codeCreditClass;
    private String classroom;
    private String numberOfStudentLTC;
    private String codeSubject;
    private String teacher;
    private  String group;
    private String combination;
    private Long credit;
}
