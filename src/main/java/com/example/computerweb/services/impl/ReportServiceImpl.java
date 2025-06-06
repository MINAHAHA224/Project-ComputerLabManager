package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.calendarResponse.CalendarManagementDto;
import com.example.computerweb.DTO.dto.roomResponse.RoomManagementDto;
import com.example.computerweb.DTO.requestBody.reportRequest.ReportFilterRequest;
import com.example.computerweb.models.entity.AccountEntity;
import com.example.computerweb.repositories.IAccountRepository;
import com.example.computerweb.repositories.ICalendarRepository;
import com.example.computerweb.repositories.IRoomRepository;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.services.IRoomService;
import com.example.computerweb.services.IReportService;
import com.example.computerweb.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements IReportService {

    private final ICalendarService calendarService;
    private final IRoomService roomService; // Giả sử roomService có hàm getAll trả về DTO
    private final ICalendarRepository calendarRepository;
    private final IRoomRepository roomRepository;
    private final IAccountRepository accountRepository; // Thêm repo này để lấy tên người dùng
    // === BÁO CÁO LỊCH ===
    @Override
    public List<CalendarManagementDto> getCalendarReportData(ReportFilterRequest filter) {
        List<CalendarManagementDto> allData = calendarRepository.findAllCustom();

        if (filter == null || (filter.getFromDate() == null && filter.getToDate() == null)) {
            return allData;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return allData.stream().filter(item -> {
            LocalDate itemDate = LocalDate.parse(item.getDate(), dtf);
            boolean afterFromDate = filter.getFromDate() == null || !itemDate.isBefore(filter.getFromDate());
            boolean beforeToDate = filter.getToDate() == null || !itemDate.isAfter(filter.getToDate());
            return afterFromDate && beforeToDate;
        }).collect(Collectors.toList());
    }

    @Override
    public byte[] generateCalendarReportExcel(ReportFilterRequest filter) throws IOException {
        String reportTitle = "BÁO CÁO LỊCH THỰC HÀNH";
        String[] headers = {"STT", "ID Lịch", "Môn học", "Giáo viên", "Phòng", "Ngày", "Thứ", "Tiết BĐ", "Số tiết", "Trạng thái"};
        List<CalendarManagementDto> data = getCalendarReportData(filter);

        return createStyledExcel(reportTitle, headers, data, (row, item, stt) -> {
            row.createCell(0).setCellValue(stt);
            row.createCell(1).setCellValue(item.getCalendarId());
            row.createCell(2).setCellValue(item.getNameSubject());
            row.createCell(3).setCellValue(item.getNameTeacher());
            row.createCell(4).setCellValue(item.getNameRoom());
            row.createCell(5).setCellValue(item.getDate());
            row.createCell(6).setCellValue(item.getDay());
            row.createCell(7).setCellValue(item.getLessonBegin());
            row.createCell(8).setCellValue(item.getLesson());
            row.createCell(9).setCellValue(item.getStatusCalendar());
        });
    }

    // === BÁO CÁO PHÒNG HỌC ===
    @Override
    public List<RoomManagementDto> getRoomReportData(ReportFilterRequest filter) {
        List<RoomManagementDto> allData = (List<RoomManagementDto>) roomService.handleGetAllDateRoom().getData();
        // Hiện tại chưa có filter cho phòng học, trả về tất cả
        // Hiện tại chưa có filter cho phòng, logic đã đúng là trả về tất cả
        // Nếu sau này thêm filter theo cơ sở, bạn sẽ thêm logic ở đây
        // Ví dụ:
    /*
    if (filter != null && filter.getFacilityId() != null) {
        return allData.stream()
            .filter(room -> room.getFacilityId().equals(filter.getFacilityId())) // Giả sử DTO có facilityId
            .collect(Collectors.toList());
    }
    */
        return allData;
    }

    @Override
    public byte[] generateRoomReportExcel(ReportFilterRequest filter) throws IOException {
        String reportTitle = "BÁO CÁO DANH SÁCH PHÒNG MÁY";
        String[] headers = {"STT", "ID Phòng", "Tên phòng", "Cơ sở", "Tổng số máy", "Số máy hoạt động"};
        List<RoomManagementDto> data = getRoomReportData(filter);

        return createStyledExcel(reportTitle, headers, data, (row, item, stt) -> {
            row.createCell(0).setCellValue(stt);
            row.createCell(1).setCellValue(item.getId());
            row.createCell(2).setCellValue(item.getNameRoom());
            row.createCell(3).setCellValue(item.getFacility());
            row.createCell(4).setCellValue(item.getNumberOfComputers());
            row.createCell(5).setCellValue(item.getNumberOfComputerActive());
        });
    }

    // === PHƯƠNG THỨC CHUNG ĐỂ TẠO EXCEL CÓ STYLE ===
    @FunctionalInterface
    interface RowDataMapper<T> {
        void map(Row row, T item, int stt);
    }

    private <T> byte[] createStyledExcel(String reportTitle, String[] headers, List<T> data, RowDataMapper<T> rowDataMapper) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("BaoCao");

            // --- Định nghĩa các Style ---
            // Style cho tiêu đề chính
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Style cho thông tin meta (Người lập, Ngày in)
            Font metaFont = workbook.createFont();
            metaFont.setBold(true);
            CellStyle metaStyle = workbook.createCellStyle();
            metaStyle.setFont(metaFont);

            // Style cho header của bảng
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // --- Bắt đầu tạo các dòng ---
            int rowNum = 0;

            // 1. Tạo Tiêu đề chính
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(reportTitle);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
            titleRow.setHeightInPoints(20);

            // Bỏ trống 1 dòng
            rowNum++;

            // 2. Tạo thông tin Meta
            String userEmail = SecurityUtils.getPrincipal();
            AccountEntity account = accountRepository.findAccountEntityByEmail(userEmail).orElse(null);
            String reporterName = (account != null && account.getUser() != null)
                    ? account.getUser().getFirstName() + " " + account.getUser().getLastName()
                    : userEmail;

            Row reporterRow = sheet.createRow(rowNum++);
            reporterRow.createCell(0).setCellValue("Người lập báo cáo:");
            reporterRow.getCell(0).setCellStyle(metaStyle);
            reporterRow.createCell(1).setCellValue(reporterName);

            Row dateRow = sheet.createRow(rowNum++);
            dateRow.createCell(0).setCellValue("Ngày in:");
            dateRow.getCell(0).setCellStyle(metaStyle);
            dateRow.createCell(1).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

            // Bỏ trống 1 dòng
            rowNum++;

            // 3. Tạo Header cho bảng
            Row headerRow = sheet.createRow(rowNum++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 4. Điền dữ liệu vào bảng
            int stt = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowNum++);
                rowDataMapper.map(row, item, stt++);
            }

            // 5. Tự động điều chỉnh độ rộng các cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}