package com.example.computerweb.DTO.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestTkResponseDto {

    CalendarResponseFields dataBase;
    RequestTicketResponseDto requestTicket;
}
