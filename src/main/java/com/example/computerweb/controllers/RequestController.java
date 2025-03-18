package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.*;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketChangeDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketManagementRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRentDto;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.services.ITicketRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Request management for GVU|CSVC , RequestTickets and Notification for GV")
public class RequestController {
    private final ITicketRequestService iTicketRequestService;
    private  final ICalendarService iCalendarService;

    @GetMapping("/requestManagement")
    @Operation(summary = "Show all request of GV" , description = "Only for GVU", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseData<List<TicketResponseMgmDto>> getRequestManager (){
        List<TicketResponseMgmDto> results = this.iTicketRequestService.handleGetAllDataForRqManagementPage();
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Execute success" ,results );
    }
    @PostMapping("/requestManagement")
    @Operation(summary = "This feature for GVU|CSVC" , description = "If Approval just post 2 field id,status | " +
            "If REJECT post 3 field id , status , noteInformation  ", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseData<?> postRequestManager (@Valid @RequestBody TicketManagementRequestDto ticketManagementRequestDto){
        ResponseEntity<String> handleTicketRequest =  this.iTicketRequestService.HandleTicketRequest(ticketManagementRequestDto);
        return new ResponseSuccess<>(HttpStatus.OK.value(), handleTicketRequest.getBody()  );
    }

    @Operation(summary = "This feature only for GV" , description = "When GV action change calendar", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/requestChangeCalendar/{calendarId}")
    public  ResponseData<?> getCreateTicket (@PathVariable("calendarId") Long calendarId){
        CalendarResponseDto   data = this.iTicketRequestService.handleGetCreateTicketChangeCalendar(calendarId);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Execute success" ,data );
    }

    @Operation(summary = "This feature only for GV" , description = "When GV filed full input  then post values back", parameters = {
            @Parameter(
                    name = "Authorization",
                    description = "Bearer ",
                    in = ParameterIn.HEADER,
                    required = true // hoặc true nếu bắt buộc
            )
    })
    @PostMapping("/requestChangeCalendar")
    public ResponseData<?> postCreateTicket (@Valid  @RequestBody TicketChangeDto ticketChangeDto){
        ResponseEntity<String> handleCreateTicket = this.iTicketRequestService.handlePostCreateTicketChangeCalendar(ticketChangeDto);
        return new ResponseSuccess<>(HttpStatus.OK.value(), handleCreateTicket.getBody());
    }

    @Operation(summary = "This feature only for GV" , description = "When GV action rent room", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/requestRentRoom")
    public  ResponseData<?> getPageRentRoom (){
        CalendarResponseFields   calendarResponseFields = this.iCalendarService.handleGetDataForCreatePage();
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Execute success " ,calendarResponseFields);
    }

    @Operation(summary = "This feature only for GV" , description = "When GV action rent room", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/requestRentRoom")
    public ResponseData<?> postRequestRentRoom (@Valid @RequestBody TicketRentDto ticketRentDto){
        ResponseEntity<String> handleCreateRentRoom = this.iTicketRequestService.handlePostCreateTicketRentRoom(ticketRentDto);
        return new ResponseSuccess<>(HttpStatus.OK.value(),  handleCreateRentRoom.getBody());
    }

    @Operation(summary = "requestTickets", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/requestTickets")
    public ResponseData<List<RequestTicketResponseDto>> getRequestTickets (){
        List<RequestTicketResponseDto> data = this.iTicketRequestService.handleGetAllRequestTicketGV();

        return  new ResponseSuccess<>(HttpStatus.OK.value(), "Execute success" , data);
    }

    @Operation(summary = "idTicket", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/requestTickets/{idTicketRequest}")
    public ResponseData<RequestTkResponseDto> getOneRequestTickets (@PathVariable("idTicketRequest") Long idTicketRequest){
        RequestTkResponseDto data = this.iTicketRequestService.handleGetRequestTicketGV(idTicketRequest);

        return  new ResponseSuccess<>(HttpStatus.OK.value(), "Execute success" , data);
    }

    @Operation(summary = "delete ticket", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/requestTickets/delete/{idTicketRequest}")
    public ResponseData<?> deleteRequestTickets (@PathVariable("idTicketRequest") String idTicketRequest){
        ResponseEntity<String> handleDelete = this.iTicketRequestService.handleDeleteOneOrMoreTicketRequest(idTicketRequest);

        return  new ResponseSuccess<>(HttpStatus.OK.value(), handleDelete.getBody() );
    }

    @Operation(summary = "This feature only for GV" , description = "When GV action watch notification", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/notification")
    public ResponseData< List<NotificationResponseDto>> getNotification (){
        List<NotificationResponseDto> dataNote = this.iTicketRequestService.handleGetAllNotificationOfUser();

        return new ResponseSuccess<>(HttpStatus.OK.value() , "Execute success" ,dataNote ) ;
    }

    @Operation(summary = "notification" , security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/notification/{notificationId}")
    public ResponseData<NotificationResponseDto> postNotification (@PathVariable("notificationId") Long notificationId){
        NotificationResponseDto handleChangeStatus = this.iTicketRequestService.handleChangeStatusNote(notificationId);
        return new ResponseSuccess<>(HttpStatus.OK.value() , "Execute success" , handleChangeStatus);
    }

    @Operation(summary = "delete notification" , security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/notification/delete/{notificationId}")
    public ResponseData<?> deleteNotification (@PathVariable("notificationId") String notificationId){
        ResponseEntity<String> handleDelete = this.iTicketRequestService.handleDeleteOneOrMoreNote(notificationId);
        return new ResponseSuccess<>(HttpStatus.OK.value() ,handleDelete.getBody() );
    }
}
