package com.example.computerweb.DTO.requestBody.calendarRequest;

import com.example.computerweb.models.enums.PurposeUse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalendarRequestDto {
    @JsonProperty("id")
    @Schema(type = "Long",  example = "1")
    private Long id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(type = "string", format = "date", example = "2025-02-19")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("purposeUse")
    @Schema(type = "string", example = "LICH_CHINH_THUC")
    private PurposeUse purposeUse;

    @JsonProperty("teacher")
    @Schema(type = "Long", example = "1")
    private Long  teacherId;

    @Schema(type = "array", example = "[1,2]")
    @JsonProperty("room")
    private List<Long> roomId;

    @Schema(type = "Long", example = "1")
    @JsonProperty("classroom")
    private Long classroomId;

    @Schema(type = "Long", example = "1")
    @JsonProperty("practiceCase")
    private Long practiceCaseId;

    @Schema(type = "Long", example = "1")
    @JsonProperty("subject")
    private Long subjectId;
}
