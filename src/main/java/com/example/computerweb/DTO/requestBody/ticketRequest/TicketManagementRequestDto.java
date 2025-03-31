package com.example.computerweb.DTO.requestBody.ticketRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketManagementRequestDto {

    @Schema(type = "Long" , example = "1")
    private Long ticketId;

    @Schema(type = "String" , example = "APPROVAL|REJECT")
    private String status;

    @Schema(type = "String" , example = "Calendar of this week has been fulling, please try next week!!!")
    private String noteNotification;


}
