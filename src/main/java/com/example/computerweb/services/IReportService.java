package com.example.computerweb.services;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarManagementDto;
import com.example.computerweb.DTO.dto.roomResponse.RoomManagementDto;
import com.example.computerweb.DTO.requestBody.reportRequest.ReportFilterRequest;

import java.io.IOException;
import java.util.List;
public interface IReportService {
    List<CalendarManagementDto> getCalendarReportData(ReportFilterRequest filter);
    byte[] generateCalendarReportExcel(ReportFilterRequest filter) throws IOException;

    List<RoomManagementDto> getRoomReportData(ReportFilterRequest filter);
    byte[] generateRoomReportExcel(ReportFilterRequest filter) throws IOException;

    // Thêm các phương thức cho báo cáo lớp tín chỉ ở đây
}
