package com.example.computerweb.DTO.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TicketResponseMgmDto {

    private String idTicket;
    private String dateSent;
    private String teacher;
    private String typeRequest;
    private String noteTicket;
    private String dateOld;
    private String dateNew;
    private String practiceCaseOld;
    private String practiceCaseNew;
    private String nameRoomOld;
    private String nameRoomNew;
    private String nameClassroom;
    private String nameSubject;
    private String status;
}
