package com.example.computerweb.services;

import com.example.computerweb.DTO.dto.CalendarResponseDto;
import com.example.computerweb.DTO.dto.NotificationResponseDto;
import com.example.computerweb.DTO.dto.TicketResponseMgmDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketChangeDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketManagementRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ITicketRequestService {

    List<TicketResponseMgmDto> handleGetAllDataForRqManagementPage ();

    ResponseEntity<String> HandleTicketRequest (TicketManagementRequestDto ticketManagementRequestDto);

    CalendarResponseDto handleGetCreateTicketChangeCalendar ( Long calendarId);

    ResponseEntity<String>  handlePostCreateTicketChangeCalendar (TicketChangeDto ticketChangeDto);

    ResponseEntity<String> handlePostCreateTicketRentRoom (TicketRentDto ticketRentDto);

    List<NotificationResponseDto> handleGetAllNotificationOfUser ( );

    NotificationResponseDto handleChangeStatusNote (Long notificationId);

    ResponseEntity<String> handleDeleteOneOrMoreNote (String noteId);
}
