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

    @JsonProperty("id")
    @Schema(type = "String" , example = "1")
    private Long idCalendar;

    @JsonProperty("typeRequest")
    @Schema(type = "String" , example = "2")
    private Long typeRequest;

    @JsonProperty("dateNew")
    @Schema(type = "String" , example = "2025-03-25")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateNew;


    @JsonProperty("practiceCaseNew")
    @Schema(type = "Long" , example = "2")
    @NotNull(message = "PracticeCase must not be null")
    private Long practiceCaseNew;

    @JsonProperty("listRoomNew")
    @NotNull(message = "Room must not be null")
    @Schema(type = "array", example = "[1,2]")
    private List<Long> listRoomNew ;

    @JsonProperty("note")
    @NotBlank(message = "Please fill the note that why you need this request")
    @Schema(type = "String" , example = "Changle calendar because i have take care my mom sick")
    private String note;

}
