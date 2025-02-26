package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.CalendarManagementDto;
import com.example.computerweb.DTO.dto.CalendarResponseDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestDto;
import com.example.computerweb.models.entity.CalendarEntity;
import com.example.computerweb.models.enums.PurposeUse;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.services.IUserService;
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
@Tag(name = "CalendarManagement only for GVU")
@RequestMapping("/calendarManagement")
public class CalendarController {
    private final ICalendarService iCalendarService;
    @GetMapping("")
    public ResponseData<List<CalendarManagementDto>> getCalendarManagement (){
       List<CalendarManagementDto> datas = this.iCalendarService.handleGetAllDataCalendar();
            return new ResponseData<>(HttpStatus.OK.value() , "Execute Success" , datas );
    }

    @GetMapping("/create")
    public ResponseData<Map<String, Map<String ,String>>> getCreateCalendarManagement (){
        Map<String, Map<String ,String>> data = this.iCalendarService.handleGetDataForCreatePage();
        return new ResponseSuccess<>(HttpStatus.OK.value(),"Execute data success" ,data );
    }

    @PostMapping("/create")
    public ResponseData<?> createCalendar (@RequestBody CalendarRequestDto calendarRequestDto){
         this.iCalendarService.handleCreateCalendar(calendarRequestDto);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Create calendar success");
    }


    @GetMapping("/update/{calendarId}")
    public ResponseData<CalendarResponseDto> getUpdateCalendar (@PathVariable("calendarId") Long calendarId ){
            CalendarResponseDto calendarManagementDto = this.iCalendarService.handleGetDataForUpdatePage(calendarId);
            return new ResponseSuccess<>(HttpStatus.OK.value() , "Execute data success" , calendarManagementDto  );
    }

    @PostMapping ("/update")
    public ResponseData<?>  postUpdateCalendar (@RequestBody CalendarRequestDto calendarRequestDto){
    this.iCalendarService.handleUpdateCalendar(calendarRequestDto);

        return new ResponseSuccess<>(HttpStatus.OK.value(), "Update calendar success");
    }

    // Only sent this to API , don't need next to new page
    @PostMapping  ("/delete/{calendarId}")
    public ResponseData<?>  postDeleteCalendar (@PathVariable("calendarId") Long calendarId){
        this.iCalendarService.handleDeleteCalendar(calendarId);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Delete calendar success");
    }


    
}
