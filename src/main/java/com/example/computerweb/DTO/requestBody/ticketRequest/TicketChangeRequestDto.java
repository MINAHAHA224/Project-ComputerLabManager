package com.example.computerweb.DTO.requestBody.ticketRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketChangeRequestDto {
    @Schema(type = "Long" , example = "15")
    @NotNull
    private Long calendarIdToChange; // ID của LichThucHanh gốc
    @Schema(type = "Long" , example = "12")
    @NotNull
    private Long newWeekSemesterId;
    @Schema(type = "Long" , example = "3")
    @NotNull
    private Long newDayId;
    @Schema(type = "Long" , example = "2")
    @NotNull
    private Long newPracticeCaseBeginId;
    private String newPurposeUse; // Ghi chú mới cho lịch (nếu có) hoặc lý do thay đổi
}
