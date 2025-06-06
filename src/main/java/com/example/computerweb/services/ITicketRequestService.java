package com.example.computerweb.services;

import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseDto;
import com.example.computerweb.DTO.dto.notificationResponse.NotificationDetailResponseDto;
import com.example.computerweb.DTO.dto.notificationResponse.NotificationResponseDto;
import com.example.computerweb.DTO.dto.requestTicketResponse.RequestTicketResponseDto;
import com.example.computerweb.DTO.dto.ticketResponse.TicketResponseMgmDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.requestBody.roomRequest.TicketCancelCalendarRequestDto;
import com.example.computerweb.DTO.requestBody.roomRequest.TicketChangeRoomRequestDto;
import com.example.computerweb.DTO.requestBody.roomRequest.TicketRentRoomRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketApprovalDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketChangeRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketChangeDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketManagementRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRentDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRequestOneDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ITicketRequestService {

    List<TicketRequestOneDto> handleGetAllDataForRqManagementPage ();

    TicketResponseMgmDto handleGetDetailRequest (Long ticketId);


    ResponseEntity<String> handleTicketRequest (TicketManagementRequestDto ticketManagementRequestDto);

    ResponseEntity<String> handleCreateTicketDeleteRoom ( Long calendarId , String message);

    CalendarResponseDto handleGetCreateTicketChangeCalendar (Long calendarId);

    ResponseEntity<String>  handlePostCreateTicketChangeCalendar (TicketChangeDto ticketChangeDto);



    ResponseEntity<String> handlePostCreateTicketRentRoom (TicketRentDto ticketRentDto);
    ResponseEntity<String> handlePostDeleteTicketRentRoom (Long calendarId);
    List<NotificationResponseDto> handleGetAllNotificationOfUser ( );

    NotificationDetailResponseDto handleChangeStatusNote (Long notificationId);

    ResponseEntity<String> handleDeleteOneOrMoreNote (String noteId);

    List<RequestTicketResponseDto> handleGetAllRequestTicketGV();

    TicketResponseMgmDto handleGetRequestTicketGV(Long id);

    ResponseEntity<String> handleDeleteOneOrMoreTicketRequest ( String  requestTicketId );

    // test changeCalendar
    ResponseData<?> createChangeCalendarTicket(TicketChangeRequestDto changeRequestDto);

    ResponseData<?> processChangeCalendarTicketApproval(TicketApprovalDto approvalDto);


    // testRentRom and cancelRoom

    ResponseData<?> createRentRoomTicket(TicketRentRoomRequestDto rentRequestDto);

    ResponseData<?> processRentRoomTicketApproval(TicketApprovalDto approvalDto); // Xử lý duyệt phiếu mượn phòng

    ResponseData<?> createChangeRoomTicket(TicketChangeRoomRequestDto changeRequestDto);
    ResponseData<?> processChangeRoomTicketApproval(TicketApprovalDto approvalDto); // Xử lý duyệt phiếu thay phòng



}
