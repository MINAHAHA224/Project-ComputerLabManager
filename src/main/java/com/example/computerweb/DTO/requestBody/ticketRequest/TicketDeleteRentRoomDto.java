package com.example.computerweb.DTO.requestBody.ticketRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Validated
public class TicketDeleteRentRoomDto {

    @JsonProperty("calendarId")
    private Long calendarId;

    @JsonProperty("message")
    @NotBlank(message = "Message must not be blank")
    private String message;
}
