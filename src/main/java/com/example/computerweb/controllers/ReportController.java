package com.example.computerweb.controllers;


import com.example.computerweb.DTO.dto.calendarResponse.CalendarManagementDto;
import com.example.computerweb.DTO.dto.roomResponse.RoomManagementDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.reportRequest.ReportFilterRequest;
import com.example.computerweb.services.IReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Report Management")
public class ReportController {

    private final IReportService reportService;

    // --- Calendar Report Endpoints ---
    @PostMapping("/calendar/preview")
    @PreAuthorize("hasAnyRole('GVU', 'TK')")
    public ResponseData<List<CalendarManagementDto>> getCalendarReportPreview(@RequestBody ReportFilterRequest filter) {
        List<CalendarManagementDto> data = reportService.getCalendarReportData(filter);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Tải dữ liệu xem trước thành công", data);
    }

    @PostMapping("/calendar/download")
    @PreAuthorize("hasAnyRole('GVU', 'TK')")
    public ResponseEntity<byte[]> downloadCalendarReport(@RequestBody ReportFilterRequest filter) throws IOException {
        byte[] excelBytes = reportService.generateCalendarReportExcel(filter);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "BaoCaoLichThucHanh.xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

    // --- Room Report Endpoints ---
    @PostMapping("/room/preview")
    @PreAuthorize("hasRole('CSVC')")
    public ResponseData<List<RoomManagementDto>> getRoomReportPreview(@RequestBody ReportFilterRequest filter) {
        List<RoomManagementDto> data = reportService.getRoomReportData(filter);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Tải dữ liệu xem trước thành công", data);
    }

    @PostMapping("/room/download")
    @PreAuthorize("hasRole('CSVC')")
    public ResponseEntity<byte[]> downloadRoomReport(@RequestBody ReportFilterRequest filter) throws IOException {
        byte[] excelBytes = reportService.generateRoomReportExcel(filter);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "BaoCaoPhongMay.xlsx");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}