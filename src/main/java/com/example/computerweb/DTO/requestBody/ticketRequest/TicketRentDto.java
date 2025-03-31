package com.example.computerweb.DTO.requestBody.ticketRequest;

import com.example.computerweb.models.enums.PurposeUse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Validated
public class TicketRentDto {



    @JsonProperty("typeRequestId")
    @Schema(type = "Long" , example = "2")
    private Long typeRequestId;



    @JsonProperty("weekSemesterId")
    @Schema(type = "Long" , example = "1")
    private Long weekSemesterId;

    @JsonProperty("day")
    @Schema(type = "Long" , example = "3")
    private Long day;


    @JsonProperty("caseBeginId")
    @Schema(type = "Long" , example = "1")
    private Long caseBeginId;


    @JsonProperty("allCase")
    @Schema(type = "Long" , example = "4")
    private Long allCase;

    @JsonProperty("roomId")
    @Schema(type = "Long" , example = "4")
    private Long roomId;

    @NotBlank(message = "PurposeUse must not be blank")
    @JsonProperty("purposeUse")
    @Schema(type = "String" , example = "hm.....")
    private String purposeUse;
}
