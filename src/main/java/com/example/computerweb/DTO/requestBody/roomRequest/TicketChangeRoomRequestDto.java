package com.example.computerweb.DTO.requestBody.roomRequest;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketChangeRoomRequestDto {
    @NotNull
    private Long calendarId; // ID của LopTinChi nếu mượn cho LTC, có thể null


    private String purposeUse; // Lý do mượn
}
