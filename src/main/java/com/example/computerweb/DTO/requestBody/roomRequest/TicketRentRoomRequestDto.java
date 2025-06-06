package com.example.computerweb.DTO.requestBody.roomRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketRentRoomRequestDto {

    @Schema(type = "Long" , example = "22")
    @NotNull
    private Long weekSemesterId;
    @Schema(type = "Long" , example = "12")
    @NotNull
    private Long dayId;
    @Schema(type = "Long" , example = "1")
    @NotNull
    private Long practiceCaseBeginId;
    @NotNull
    @Schema(type = "Long" , example = "4")
    private Long allCase; // Số tiết muốn mượn

    private String purposeUse; // Lý do mượn
}
