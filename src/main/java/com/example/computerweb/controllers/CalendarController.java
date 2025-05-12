package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.calendarResponse.CalendarManagementDto;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseDto;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseFields;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseFailure;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestDto;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestOneDto;
import com.example.computerweb.services.ICalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "CalendarManagement only for GVU , Calendar for GVU|CSVC|GV")
public class CalendarController {

    private final ICalendarService iCalendarService;



    @GetMapping("/calendar")
    @Operation(summary = "Show all calendar" , description = "If GVU show all data , " +
            "If CSVC show some fields ex : Date,Room, PracticeCase , Time , " +
            "If GV only show data calendar of GV ", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseData<List<CalendarManagementDto>> getCalendar (){
        List<CalendarManagementDto> data = this.iCalendarService.handleGetAllDataCalendar();

        return new ResponseData<>(HttpStatus.OK.value() , "Thực hiện thành công" , data );
    }
    @Operation(summary = "Show all data calendar admin page" , description = "GVU can setting update|delete on this page", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/calendarManagement")
    public ResponseData<List<CalendarManagementDto>> getCalendarManagement (){
       List<CalendarManagementDto> data = this.iCalendarService.handleGetAllDataCalendar();
            return new ResponseData<>(HttpStatus.OK.value() , "Thực hiện thành công" , data );
    }

    // Create Calendar
    @Operation(summary = "Page create calendar of GVU" , description = "GVU can select field calendar on this page", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/calendarManagement/create")
    public ResponseData<CalendarResponseFields> getCreateCalendarManagement (){
        CalendarResponseFields data = this.iCalendarService.handleGetDataForCreatePage();
        return new ResponseSuccess<>(HttpStatus.OK.value(),"Thực hiện thành công" ,data );
    }

    @Operation(summary = "GVU Choose WeekStudy" , description = "GVU Choose WeekStudy when choose CreditClass", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/calendarManagement/{semesterYear}")
    public ResponseData<?> getWeekStudyForCreateCreditClass(@PathVariable("semesterYear") String semesterYear){
        ArrayList<Map<String, String>> data = this.iCalendarService.handleWeekStudyForCreateCreditClass(semesterYear);
        return new ResponseSuccess<>(HttpStatus.OK.value(),"Thực hiện thành công" ,data );
    }

    @Operation(summary = "Post info calendar, only of GVU", description = "GVU create calendar on this page", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/calendarManagement/create")
    public ResponseData<?> getCreateCalendar(@Valid @RequestBody CalendarRequestDto calendarRequestDto) {
        return this.iCalendarService.handleCreateCalendar(calendarRequestDto);
    }
    // Create room
//    @Operation(summary = "Page create calendar of GVU" , description = "GVU can select field calendar on this page", security = @SecurityRequirement(name = "bearerAuth"))
//    @GetMapping("/calendarManagement/createRoom")
//    public ResponseData<CalendarResponseFields> getCreateRoomCalendarManagement (){
//        CalendarResponseFields data = this.iCalendarService.handleGetDataForCreateRoomPage();
//        return new ResponseSuccess<>(HttpStatus.OK.value(),"Execute data success" ,data );
//    }
//
//    @Operation(summary = "Post info calendar, only of GVU" , description = "GVU create calendar on this page", security = @SecurityRequirement(name = "bearerAuth"))
//    @PostMapping("/calendarManagement/createRoom")
//    public ResponseData<?> getCreateRoom (@RequestBody CalendarRequestRoomDto calendarRequestRoomDto){
//        ResponseEntity<String> createRoom =  this.iCalendarService.handleCreateRoom(calendarRequestRoomDto);
//        if ( createRoom.getStatusCode() == HttpStatus.BAD_REQUEST){
//            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), createRoom.getBody());
//        }else {
//            return new ResponseSuccess<>(HttpStatus.OK.value(), createRoom.getBody());
//        }
//
//    }

    // UPDATE BOTH lich chinh thuc , lich muon phong
    @Operation(summary = "Show info calendar of user" , description = "GVU can update info calendar of user on this page", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/calendarManagement/update/{calendarId}")
    public ResponseData<CalendarResponseDto> getUpdateCalendar (@PathVariable("calendarId") Long calendarId ){
            CalendarResponseDto calendarManagementDto = this.iCalendarService.handleGetDataForUpdatePage(calendarId);
            return new ResponseSuccess<>(HttpStatus.OK.value() , "Thực hiện thành công" , calendarManagementDto  );
    }

    @Operation(summary = "Post info  calendar of user" , security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping ("/calendarManagement/update")
    public ResponseData<?>  postUpdateCalendar (@RequestBody @Valid CalendarRequestOneDto calendarRequestOneDto){
        return   this.iCalendarService.handleUpdateCalendar(calendarRequestOneDto);


    }

    // Only sent this to API , don't need next to new page
    @Operation(summary = "Feature delete of GVU" , security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping  ("/calendarManagement/delete/{calendarId}")
    public ResponseData<?>  postDeleteCalendar (@PathVariable("calendarId") String calendarId){
        this.iCalendarService.handleDeleteCalendar(calendarId);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Xóa lịch thành công");
    }


    
}
