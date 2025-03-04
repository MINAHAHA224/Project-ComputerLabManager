package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.CalendarManagementDto;
import com.example.computerweb.DTO.dto.CalendarResponseDto;
import com.example.computerweb.DTO.dto.CalendarResponseFields;
import com.example.computerweb.DTO.dto.HomeResponseDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestDto;
import com.example.computerweb.models.entity.CalendarEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.models.enums.PurposeUse;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.services.IUserService;
import com.example.computerweb.utils.SecurityUtils;
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
public class CalendarController {
    private final ICalendarService iCalendarService;
    private  final IUserService iUserService;

    @GetMapping("/home")
    public ResponseData<HomeResponseDto> getHomePage (){

        Map<String , String> dataUser = this.iUserService.handleGetDataUserCurrent();
        HomeResponseDto homeResponseDto = new HomeResponseDto();
        homeResponseDto.setDataUser(dataUser);
        return new ResponseData<>(HttpStatus.OK.value() , "Execute Success" , homeResponseDto );
    }
    @GetMapping("/calendar")
    public ResponseData<List<CalendarManagementDto>> getCalendar (){
        List<CalendarManagementDto> data = this.iCalendarService.handleGetAllDataCalendar();

        return new ResponseData<>(HttpStatus.OK.value() , "Execute Success" , data );
    }
    @GetMapping("/calendarManagement")
    public ResponseData<List<CalendarManagementDto>> getCalendarManagement (){
       List<CalendarManagementDto> data = this.iCalendarService.handleGetAllDataCalendar();
            return new ResponseData<>(HttpStatus.OK.value() , "Execute Success" , data );
    }

    @GetMapping("/calendarManagement/create")
    public ResponseData<CalendarResponseFields> getCreateCalendarManagement (){
        CalendarResponseFields data = this.iCalendarService.handleGetDataForCreatePage();
        return new ResponseSuccess<>(HttpStatus.OK.value(),"Execute data success" ,data );
    }

    @PostMapping("/calendarManagement/create")
    public ResponseData<?> createCalendar (@RequestBody CalendarRequestDto calendarRequestDto){
         this.iCalendarService.handleCreateCalendar(calendarRequestDto);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Create calendar success");
    }


    @GetMapping("/calendarManagement/update/{calendarId}")
    public ResponseData<CalendarResponseDto> getUpdateCalendar (@PathVariable("calendarId") Long calendarId ){
            CalendarResponseDto calendarManagementDto = this.iCalendarService.handleGetDataForUpdatePage(calendarId);
            return new ResponseSuccess<>(HttpStatus.OK.value() , "Execute data success" , calendarManagementDto  );
    }

    @PostMapping ("/calendarManagement/update")
    public ResponseData<?>  postUpdateCalendar (@RequestBody CalendarRequestDto calendarRequestDto){
    this.iCalendarService.handleUpdateCalendar(calendarRequestDto);

        return new ResponseSuccess<>(HttpStatus.OK.value(), "Update calendar success");
    }

    // Only sent this to API , don't need next to new page
    @PostMapping  ("/calendarManagement/delete/{calendarId}")
    public ResponseData<?>  postDeleteCalendar (@PathVariable("calendarId") Long calendarId){
        this.iCalendarService.handleDeleteCalendar(calendarId);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Delete calendar success");
    }


    
}
