package com.example.computerweb.services;

import com.example.computerweb.DTO.dto.TicketResponseMgmDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketManagementRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ITicketRequestService {

    List<TicketResponseMgmDto> handleGetAllDataForRqManagementPage ();

    ResponseEntity<String> HandleTicketRequest (TicketManagementRequestDto ticketManagementRequestDto);
}
