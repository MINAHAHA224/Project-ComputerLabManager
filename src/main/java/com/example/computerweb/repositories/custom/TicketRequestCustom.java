package com.example.computerweb.repositories.custom;

import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRequestOneDto;

import java.util.List;

public interface TicketRequestCustom {


    List<TicketRequestOneDto>  findListTicketRequestForCSVC ();
}
