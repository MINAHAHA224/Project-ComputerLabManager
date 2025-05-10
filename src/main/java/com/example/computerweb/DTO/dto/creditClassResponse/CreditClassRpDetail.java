package com.example.computerweb.DTO.dto.creditClassResponse;

import com.example.computerweb.DTO.dto.userResponse.TeacherRpDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreditClassRpDetail {
    private Long creditClassId;
    private String codeCreditClass;
    private String classroom;
    private String numberOfStudentLTC;
    private String codeSubject;
    private String teacher;
    private String group;
    private String combination;
    private String credit;

    private List<TeacherRpDto> listTeacher;
}
