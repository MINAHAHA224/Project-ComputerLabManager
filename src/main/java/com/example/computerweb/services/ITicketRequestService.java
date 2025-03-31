package com.example.computerweb.services;

import com.example.computerweb.DTO.dto.*;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketChangeDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketManagementRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRentDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRequestOneDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ITicketRequestService {

    List<TicketRequestOneDto> handleGetAllDataForRqManagementPage ();

    TicketResponseMgmDto handleGetDetailRequest (Long ticketId);


    ResponseEntity<String> HandleTicketRequest (TicketManagementRequestDto ticketManagementRequestDto);

    ResponseEntity<String> handleCreateTicketDeleteRoom ( Long calendarId , String message);

    CalendarResponseDto handleGetCreateTicketChangeCalendar ( Long calendarId);

    ResponseEntity<String>  handlePostCreateTicketChangeCalendar (TicketChangeDto ticketChangeDto);



    ResponseEntity<String> handlePostCreateTicketRentRoom (TicketRentDto ticketRentDto);
    ResponseEntity<String> handlePostDeleteTicketRentRoom (Long calendarId);
    List<NotificationResponseDto> handleGetAllNotificationOfUser ( );

    NotificationDetailResponseDto handleChangeStatusNote (Long notificationId);

    ResponseEntity<String> handleDeleteOneOrMoreNote (String noteId);

    List<RequestTicketResponseDto> handleGetAllRequestTicketGV();

    TicketResponseMgmDto handleGetRequestTicketGV(Long id);

    ResponseEntity<String> handleDeleteOneOrMoreTicketRequest ( String  requestTicketId );
}
