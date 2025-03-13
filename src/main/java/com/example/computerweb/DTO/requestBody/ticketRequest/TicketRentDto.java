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



    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(type = "string", format = "date", example = "2025-02-19")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;



    @JsonProperty("teacher")
    @Schema(type = "Long", example = "1")

    private Long  teacherId;

    @Schema(type = "array", example = "[1,2]")
    @JsonProperty("room")
    @NotNull(message = "Room must not be null")
    private List<Long> roomId;

    @Schema(type = "Long", example = "1")
    @JsonProperty("classroom")
    @NotNull(message = "Classroom must not be null")
    private Long classroomId;

    @Schema(type = "Long", example = "1")
    @JsonProperty("practiceCase")
    @NotNull(message = "PracticeCase must not be null")
    private Long practiceCaseId;

    @Schema(type = "Long", example = "1")
    @JsonProperty("subject")
    @NotNull(message = "Subject must not be null")
    private Long subjectId;

    @JsonProperty("note")
    @NotBlank(message = "Please fill the note that why you need this request")
    @Schema(type = "String" , example = "I want to rent this room , at ... , to demo project to student on class ...")
    private String note;
}
