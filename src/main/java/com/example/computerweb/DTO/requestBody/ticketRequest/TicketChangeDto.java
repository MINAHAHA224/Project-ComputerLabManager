package com.example.computerweb.DTO.requestBody.ticketRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Validated
public class TicketChangeDto {

    @JsonProperty("typeRequestId")
    @Schema(type = "Long" , example = "2")
    private Long typeRequestId;

    @JsonProperty("calendarId")
    @Schema(type = "Long" , example = "1")
    private Long calendarId;


    @JsonProperty("weekSemesterId")
    @Schema(type = "Long" , example = "1")
    private Long weekSemesterId;

    @JsonProperty("day")
    @Schema(type = "Long" , example = "3")
    private Long day;


    @JsonProperty("caseBeginId")
    @Schema(type = "Long" , example = "1")
    private Long caseBeginId;



    @NotBlank(message = "PurposeUse must not be blank")
    @JsonProperty("purposeUse")
    @Schema(type = "String" , example = "hm.....")
    private String purposeUse;



}
