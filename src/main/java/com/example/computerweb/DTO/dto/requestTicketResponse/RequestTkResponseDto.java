package com.example.computerweb.DTO.dto.requestTicketResponse;

import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseFields;
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
