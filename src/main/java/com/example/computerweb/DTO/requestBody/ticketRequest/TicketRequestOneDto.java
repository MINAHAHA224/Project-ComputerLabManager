package com.example.computerweb.DTO.requestBody.ticketRequest;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TicketRequestOneDto {
    private String requestId;
    private String typeRequestId;
    private String dateRequest;
    private String userRequest;
    private String status;
}
