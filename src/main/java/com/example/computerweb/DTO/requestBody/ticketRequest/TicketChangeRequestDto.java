package com.example.computerweb.DTO.requestBody.ticketRequest;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketChangeRequestDto {
    @NotNull
    private Long calendarIdToChange; // ID của LichThucHanh gốc
    @NotNull
    private Long newWeekSemesterId;
    @NotNull
    private Long newDayId;
    @NotNull
    private Long newPracticeCaseBeginId;
    private String newPurposeUse; // Ghi chú mới cho lịch (nếu có) hoặc lý do thay đổi
}
