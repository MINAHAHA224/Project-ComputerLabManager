package com.example.computerweb.DTO.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TicketResponseMgmDto {

    // one Side
    private String requestId;
    private String typeRequest;
    private String dateRequest;
    private String userRequest;

    private String doneCSVC;
    private String created_CSVC;
    private String modified_CSVC;

    private String doneGVU;
    private String created_GVU;
    private String modified_GVU;


    // two Side Old - New
    private String weekSemesterOld;
    private String dayOld;
    private String practiceCaseBeginOld;
    private String allCaseOld;
    private String roomOld;
    private String noteOld;

    private String weekSemesterNew;
    private String dayNew;
    private String practiceCaseBeginNew;
    private String allCaseNew;
    private String roomNew;
    private String noteNew;

}
