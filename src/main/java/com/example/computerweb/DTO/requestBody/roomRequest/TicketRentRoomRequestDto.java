package com.example.computerweb.DTO.requestBody.roomRequest;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketRentRoomRequestDto {


    @NotNull
    private Long weekSemesterId;
    @NotNull
    private Long dayId;
    @NotNull
    private Long practiceCaseBeginId;
    @NotNull
    private Long allCase; // Số tiết muốn mượn

    private String purposeUse; // Lý do mượn
}
