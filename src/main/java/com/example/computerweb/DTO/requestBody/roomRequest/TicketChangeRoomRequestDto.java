package com.example.computerweb.DTO.requestBody.roomRequest;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(type = "Long" , example = "12")
    @NotNull
    private Long calendarId; // ID của LopTinChi nếu mượn cho LTC, có thể null


    private String purposeUse; // Lý do mượn
}
