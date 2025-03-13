package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.CalendarResponseDto;
import com.example.computerweb.DTO.dto.CalendarResponseFields;
import com.example.computerweb.DTO.dto.NotificationResponseDto;
import com.example.computerweb.DTO.dto.TicketResponseMgmDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketChangeDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketManagementRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRentDto;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.services.ITicketRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Request management for GVU|CSVC")
public class RequestController {
    private final ITicketRequestService iTicketRequestService;
    private  final ICalendarService iCalendarService;

    @GetMapping("/requestManagement")
    @Operation(summary = "Show all request of GV" , description = "Only for GVU")
    public ResponseData<List<TicketResponseMgmDto>> getRequestManager (){
        List<TicketResponseMgmDto> results = this.iTicketRequestService.handleGetAllDataForRqManagementPage();
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Execute success" ,results );
    }
    @PostMapping("/requestManagement")
    @Operation(summary = "This feature for GVU|CSVC" , description = "If Approval just post 2 field id,status | " +
            "If REJECT post 3 field id , status , noteInformation  ")
    public ResponseData<?> postRequestManager (@Valid @RequestBody TicketManagementRequestDto ticketManagementRequestDto){
        ResponseEntity<String> handleTicketRequest =  this.iTicketRequestService.HandleTicketRequest(ticketManagementRequestDto);
        return new ResponseSuccess<>(HttpStatus.OK.value(), handleTicketRequest.getBody()  );
    }

    @Operation(summary = "This feature only for GV" , description = "When GV action change calendar")
    @GetMapping("/requestChangeCalendar/{calendarId}")
    public  ResponseData<?> getCreateTicket (@PathVariable("calendarId") Long calendarId){
        CalendarResponseDto   data = this.iTicketRequestService.handleGetCreateTicketChangeCalendar(calendarId);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Execute success" ,data );
    }

    @Operation(summary = "This feature only for GV" , description = "When GV filed full input  then post values back")
    @PostMapping("/requestChangeCalendar")
    public ResponseData<?> postCreateTicket (@Valid  @RequestBody TicketChangeDto ticketChangeDto){
        ResponseEntity<String> handleCreateTicket = this.iTicketRequestService.handlePostCreateTicketChangeCalendar(ticketChangeDto);
        return new ResponseSuccess<>(HttpStatus.OK.value(), handleCreateTicket.getBody());
    }

    @Operation(summary = "This feature only for GV" , description = "When GV action rent room")
    @GetMapping("/requestRentRoom")
    public  ResponseData<?> getPageRentRoom (){
        CalendarResponseFields   calendarResponseFields = this.iCalendarService.handleGetDataForCreatePage();
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Execute success " ,calendarResponseFields);
    }

    @Operation(summary = "This feature only for GV" , description = "When GV action rent room")
    @PostMapping("/requestRentRoom")
    public ResponseData<?> postRequestRentRoom (@Valid @RequestBody TicketRentDto ticketRentDto){
        ResponseEntity<String> handleCreateRentRoom = this.iTicketRequestService.handlePostCreateTicketRentRoom(ticketRentDto);
        return new ResponseSuccess<>(HttpStatus.OK.value(),  handleCreateRentRoom.getBody());
    }


    @Operation(summary = "This feature only for GV" , description = "When GV action watch notification")
    @GetMapping("/notification")
    public ResponseData< List<NotificationResponseDto>> getNotification (){
        List<NotificationResponseDto> dataNote = this.iTicketRequestService.handleGetAllNotificationOfUser();

        return new ResponseSuccess<>(HttpStatus.OK.value() , "Execute success" ,dataNote ) ;
    }

    @PostMapping("/notification/{notificationId}")
    public ResponseData<NotificationResponseDto> postNotification (@PathVariable("notificationId") Long notificationId){
        NotificationResponseDto handleChangeStatus = this.iTicketRequestService.handleChangeStatusNote(notificationId);
        return new ResponseSuccess<>(HttpStatus.OK.value() , "Execute success" , handleChangeStatus);
    }

    @DeleteMapping("/notification/delete/{notificationId}")
    public ResponseData<?> deleteNotification (@PathVariable("notificationId") String notificationId){
        ResponseEntity<String> handleDelete = this.iTicketRequestService.handleDeleteOneOrMoreNote(notificationId);
        return new ResponseSuccess<>(HttpStatus.OK.value() ,handleDelete.getBody() );
    }
}
