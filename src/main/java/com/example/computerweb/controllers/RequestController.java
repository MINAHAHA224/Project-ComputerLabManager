package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseDto;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseFields;
import com.example.computerweb.DTO.dto.notificationResponse.NotificationDetailResponseDto;
import com.example.computerweb.DTO.dto.notificationResponse.NotificationResponseDto;
import com.example.computerweb.DTO.dto.requestTicketResponse.RequestTicketResponseDto;
import com.example.computerweb.DTO.dto.ticketResponse.TicketResponseMgmDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseFailure;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.roomRequest.TicketChangeRoomRequestDto;
import com.example.computerweb.DTO.requestBody.roomRequest.TicketRentRoomRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.*;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketApprovalDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketChangeRequestDto;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.services.ITicketRequestService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Show all request of GV|CSVC" , description = "Only for GVU|CSVC", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseData<List<TicketRequestOneDto>> getRequestManager (){
        List<TicketRequestOneDto> results = this.iTicketRequestService.handleGetAllDataForRqManagementPage();
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công" ,results );
    }

    @GetMapping("/requestManagement/{ticketId}")
    public ResponseData<TicketResponseMgmDto> getDetailRequestManagement (@PathVariable("ticketId") Long ticketId){
        TicketResponseMgmDto data = this.iTicketRequestService.handleGetDetailRequest(ticketId);
        return new ResponseSuccess<>(HttpStatus.OK.value() , "Thực hiện thành công" , data);
    }

//Test
    @PostMapping("/processChangeCalendar")
    @Operation(summary = "This feature for TK" , description = "If Approval just post 2 field id,status | " +
            "If REJECT post 3 field id , status , noteInformation  ", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseData<?> postProcessChangeCalendar (@Valid @RequestBody TicketApprovalDto approvalDto){
        ResponseData<?> handleTicketRequest =  this.iTicketRequestService.processChangeCalendarTicketApproval(approvalDto);
        return handleTicketRequest;
    }

    @PostMapping("/processChangeRoom")
    @Operation(summary = "This feature for CSVC" , description = "If Approval just post 2 field id,status | " +
            "If REJECT post 3 field id , status , noteInformation  ", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseData<?> postProcessChangeRoom (@Valid @RequestBody TicketApprovalDto approvalDto){
        ResponseData<?> handleTicketRequest =  this.iTicketRequestService.processChangeRoomTicketApproval(approvalDto);
        return handleTicketRequest;
    }


    @PostMapping("/processRentRoom")
    @Operation(summary = "This feature for GVU" , description = "If Approval just post 2 field id,status | " +
            "If REJECT post 3 field id , status , noteInformation  ", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseData<?> postProcessRentRoom (@Valid @RequestBody TicketApprovalDto approvalDto){
        ResponseData<?> handleTicketRequest =  this.iTicketRequestService.processRentRoomTicketApproval(approvalDto);
        return handleTicketRequest;
    }
    //Test


    // REQUEST CHANGE CALENDAR => lich chinh thuc , lich muon phong
    @Operation(summary = "This feature only for GV" , description = "When GV action change calendar", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/requestChangeCalendar/{calendarId}")
    public  ResponseData<?> getCreateTicket (@PathVariable("calendarId") Long calendarId){
        CalendarResponseDto data = this.iTicketRequestService.handleGetCreateTicketChangeCalendar(calendarId);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công" ,data );
    }




    //Test
    @Operation(summary = "This feature only for GV" , description = "When GV filed full input  then post values back", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/requestChangeCalendar")
    public ResponseData<?>  postCreateTicket (@Valid  @RequestBody TicketChangeRequestDto changeRequestDto){
            ResponseData<?>  handleCreateTicket = this.iTicketRequestService.createChangeCalendarTicket(changeRequestDto);
        return  handleCreateTicket;
    }


    //Test

    // REQUEST CHANGE ROOM => lich chinh thuc , lich muon phong
    @Operation(summary = "This feature only for GV" , description = "When GV action change calendar", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/requestChangeRoom/{calendarId}")
    public  ResponseData<?> getCreateTicketChangeRoom (@PathVariable("calendarId") Long calendarId){
        CalendarResponseDto   data = this.iTicketRequestService.handleGetCreateTicketChangeCalendar(calendarId);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công" ,data );
    }

    @Operation(summary = "This feature only for GV" , description = "When GV filed full input  then post values back", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/requestChangeRoom")
    public ResponseData<?> postCreateTicketChangeRoom (@Valid  @RequestBody TicketChangeRoomRequestDto changeRequestDto){
        ResponseData<?> handleCreateTicket = this.iTicketRequestService.createChangeRoomTicket(changeRequestDto);
        return handleCreateTicket;
    }



    // REQUEST RENT ROOM =>  lich muon phong
    @Operation(summary = "This feature only for GV" , description = "When GV action rent room", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/requestRentRoom")
    public  ResponseData<?> getPageRentRoom (){
        CalendarResponseFields calendarResponseFields = this.iCalendarService.handleGetDataForCreateRoomPage();
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công" ,calendarResponseFields);
    }



    @Operation(summary = "This feature only for GV" , description = "When GV action rent room", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/requestRentRoom")
    public ResponseData<?> postRequestRentRoom (@Valid @RequestBody TicketRentRoomRequestDto rentRequestDto){
        ResponseData<?> handleCreateRentRoom = this.iTicketRequestService.createRentRoomTicket(rentRequestDto);
        return handleCreateRentRoom;
    }

    // REQUEST DELETE ROOM RENT





    @Operation(summary = "requestTickets", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/requestTickets")
    public ResponseData<List<RequestTicketResponseDto>> getRequestTickets (){
        List<RequestTicketResponseDto> data = this.iTicketRequestService.handleGetAllRequestTicketGV();
        return  new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công" , data);
    }

    @Operation(summary = "idTicket", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/requestTickets/{idTicketRequest}")
    public ResponseData<TicketResponseMgmDto> getOneRequestTickets (@PathVariable("idTicketRequest") Long idTicketRequest){
        TicketResponseMgmDto data = this.iTicketRequestService.handleGetRequestTicketGV(idTicketRequest);
        return  new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công" , data);
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

        return new ResponseSuccess<>(HttpStatus.OK.value() , "Thực hiện thành công" ,dataNote ) ;
    }

    @Operation(summary = "notification" , security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/notification/{notificationId}")
    public ResponseData<NotificationDetailResponseDto> postNotification (@PathVariable("notificationId") Long notificationId){
        NotificationDetailResponseDto handleChangeStatus = this.iTicketRequestService.handleChangeStatusNote(notificationId);
        return new ResponseSuccess<>(HttpStatus.OK.value() , "Thực hiện thành công" , handleChangeStatus);
    }

    @Operation(summary = "delete notification" , security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/notification/delete/{notificationId}")
    public ResponseData<?> deleteNotification (@PathVariable("notificationId") String notificationId){
        ResponseEntity<String> handleDelete = this.iTicketRequestService.handleDeleteOneOrMoreNote(notificationId);
        return new ResponseSuccess<>(HttpStatus.OK.value() ,handleDelete.getBody() );
    }
}
