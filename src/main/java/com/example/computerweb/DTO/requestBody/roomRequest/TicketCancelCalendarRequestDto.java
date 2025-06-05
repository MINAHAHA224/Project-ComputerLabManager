package com.example.computerweb.DTO.requestBody.roomRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor
public class TicketCancelCalendarRequestDto {
    @NotNull
    private Long calendarIdToCancel; // ID của LichThucHanh cần hủy
    @NotBlank
    private String reason; // Lý do hủy
}
