package com.example.computerweb.repositories.custom;

import com.example.computerweb.DTO.dto.TicketResponseMgmDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRequestOneDto;
import com.example.computerweb.models.entity.TicketRequestEntity;

import java.util.List;

public interface TicketRequestCustom {


    List<TicketRequestOneDto>  findListTicketRequestForCSVC ();
}
