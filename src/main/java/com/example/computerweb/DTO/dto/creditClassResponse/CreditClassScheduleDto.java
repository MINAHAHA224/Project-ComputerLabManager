package com.example.computerweb.DTO.dto.creditClassResponse;

import lombok.Data;

import java.util.List;

@Data
public class CreditClassScheduleDto {
    private String maMh;
    private String tenMh;
    private String nhomTo;
    private Integer soTinChi;
    private String lop;
    private List<ScheduleDetailDto> scheduleDetails;

    @Data
    public static class ScheduleDetailDto {
        private String thu;
        private String tietBatDau;
        private Integer soTiet;
        private String phong;
        private String giangVien;
        private String thoiGianHoc; // Ví dụ: "14/02/25 đến 02/05/25"
    }
}
