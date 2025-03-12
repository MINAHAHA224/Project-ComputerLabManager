package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.TicketResponseMgmDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketManagementRequestDto;
import com.example.computerweb.services.ITicketRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Request management for GVU|CSVC")
public class RequestController {
    private final ITicketRequestService iTicketRequestService;

    @GetMapping("/requestManagement")
    @Operation(summary = "Show all request of GV" , description = "Only for GVU")
    public ResponseData<List<TicketResponseMgmDto>> getRequestManager (){
        List<TicketResponseMgmDto> results = this.iTicketRequestService.handleGetAllDataForRqManagementPage();
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Execute success" ,results );
    }
    @PostMapping("/requestManagement")
    @Operation(summary = "This feature for GVU|CSVC" , description = "If Approval just post 2 field id,status | " +
            "If REJECT post 3 field id , status , noteInformation  ")
    public ResponseData<?> postRequestManager (@RequestBody TicketManagementRequestDto ticketManagementRequestDto){
        ResponseEntity<String> handleTicketRequest =  this.iTicketRequestService.HandleTicketRequest(ticketManagementRequestDto);
        return new ResponseSuccess<>(HttpStatus.OK.value(), handleTicketRequest.getBody()  );
    }
}
