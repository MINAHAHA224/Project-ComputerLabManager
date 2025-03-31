package com.example.computerweb.DTO.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreditClassEligibleDto {

    private String creditClassId;
    private String codeSubject ;
    private String nameSubject;
    private String codeClassroom;
    private String studentClassroom;
    private String lessonSum;
    private String lessonHave;
}
