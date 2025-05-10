package com.example.computerweb.DTO.dto.creditClassResponse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreditClassEligibleDto {

    private String creditClassId;
    private String codeCreditClass ;
    private String nameSubject;
    private String studentClassroom;
    private String lessonSum;
    private String lessonHave;
}
