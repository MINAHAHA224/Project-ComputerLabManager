package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.CalendarManagementDto;
import com.example.computerweb.DTO.dto.CalendarResponseDto;
import com.example.computerweb.DTO.dto.CalendarResponseFields;
import com.example.computerweb.DTO.dto.HomeResponseDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseFailure;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestDto;
import com.example.computerweb.models.entity.CalendarEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.models.enums.PurposeUse;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.services.IUserService;
import com.example.computerweb.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "CalendarManagement only for GVU , Calendar for GVU|CSVC|GV")
public class CalendarController {
    private final ICalendarService iCalendarService;



    @GetMapping("/calendar")
    @Operation(summary = "Show all calendar" , description = "If GVU show all data , " +
            "If CSVC show some fields ex : Date,Room, PracticeCase , Time , " +
            "If GV only show data calendar of GV ")
    public ResponseData<List<CalendarManagementDto>> getCalendar (){
        List<CalendarManagementDto> data = this.iCalendarService.handleGetAllDataCalendar();

        return new ResponseData<>(HttpStatus.OK.value() , "Execute Success" , data );
    }
    @Operation(summary = "Show all data calendar admin page" , description = "GVU can setting update|delete on this page")
    @GetMapping("/calendarManagement")
    public ResponseData<List<CalendarManagementDto>> getCalendarManagement (){
       List<CalendarManagementDto> data = this.iCalendarService.handleGetAllDataCalendar();
            return new ResponseData<>(HttpStatus.OK.value() , "Execute Success" , data );
    }
    @Operation(summary = "Page create calendar of GVU" , description = "GVU can select field calendar on this page")
    @GetMapping("/calendarManagement/create")
    public ResponseData<CalendarResponseFields> getCreateCalendarManagement (){
        CalendarResponseFields data = this.iCalendarService.handleGetDataForCreatePage();
        return new ResponseSuccess<>(HttpStatus.OK.value(),"Execute data success" ,data );
    }
    @Operation(summary = "Post info calendar, only of GVU" , description = "GVU create calendar on this page")
    @PostMapping("/calendarManagement/create")
    public ResponseData<?> createCalendar (@RequestBody CalendarRequestDto calendarRequestDto){
      ResponseEntity<String> createCalendar =  this.iCalendarService.handleCreateCalendar(calendarRequestDto);
      if ( createCalendar.getStatusCode() == HttpStatus.BAD_REQUEST){
          return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), createCalendar.getBody());
      }else {
          return new ResponseSuccess<>(HttpStatus.OK.value(), createCalendar.getBody());
      }

    }

    @Operation(summary = "Show info calendar of user" , description = "GVU can update info calendar of user on this page")
    @GetMapping("/calendarManagement/update/{calendarId}")
    public ResponseData<CalendarResponseDto> getUpdateCalendar (@PathVariable("calendarId") Long calendarId ){
            CalendarResponseDto calendarManagementDto = this.iCalendarService.handleGetDataForUpdatePage(calendarId);
            return new ResponseSuccess<>(HttpStatus.OK.value() , "Execute data success" , calendarManagementDto  );
    }

    @Operation(summary = "Post info  calendar of user" )
    @PostMapping ("/calendarManagement/update")
    public ResponseData<?>  postUpdateCalendar (@RequestBody CalendarRequestDto calendarRequestDto){
        ResponseEntity<String> updateCalendar =  this.iCalendarService.handleUpdateCalendar(calendarRequestDto);

        if ( updateCalendar.getStatusCode() == HttpStatus.BAD_REQUEST){
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), updateCalendar.getBody());
        }else {
            return new ResponseSuccess<>(HttpStatus.OK.value(), updateCalendar.getBody());
        }
    }

    // Only sent this to API , don't need next to new page
    @Operation(summary = "Feature delete of GVU" )
    @DeleteMapping  ("/calendarManagement/delete/{calendarId}")
    public ResponseData<?>  postDeleteCalendar (@PathVariable("calendarId") Long calendarId){
        this.iCalendarService.handleDeleteCalendar(calendarId);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Delete calendar success");
    }


    
}
