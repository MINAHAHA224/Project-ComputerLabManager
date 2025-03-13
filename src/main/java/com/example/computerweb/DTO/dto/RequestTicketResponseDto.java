package com.example.computerweb.DTO.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestTicketResponseDto {
    private String idRequestTicket;
    private String dateTimeRequest;
    private String nameUser;
    private String typeRequest;
    private String doneGVU;
    private String doneCSVC;
    private String status;

    private String noteTicket;
    private String dateOld;
    private String dateNew;
    private String practiceCaseOld;
    private String practiceCaseNew;
    private String roomOld;
    private String roomNew;
    private String classroom;
    private String subject;

}
