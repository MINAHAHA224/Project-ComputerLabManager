package com.example.computerweb.DTO.requestBody.reportRequest;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ReportFilterRequest {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    // Có thể thêm các filter khác sau này
    // private List<Long> statusIds;
    // private Long facilityId;
}
