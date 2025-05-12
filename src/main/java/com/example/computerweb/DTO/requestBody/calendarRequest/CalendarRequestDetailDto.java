package com.example.computerweb.DTO.requestBody.calendarRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalendarRequestDetailDto {
    @JsonProperty("groupId")
    @Schema(type = "Long", example = "1")
    @NotNull(message = "Tổ hợp không được để trống")
    private Long groupId;

    @JsonProperty("weekSemesterId")
    @Schema(type = "Long", example = "1")
    @NotNull(message = "Tuần học không được để trống")
    private Long weekSemesterId;

    @JsonProperty("dayId")
    @Schema(type = "Long", example = "1")
    @NotNull(message = "Thứ không được để trống")
    private Long dayId;

    @JsonProperty("practiceCaseBeginId")
    @Schema(type = "Long", example = "1")
    @NotNull(message = "Tiết bắt đầu không được để trống")
    private Long practiceCaseBeginId;

    @JsonProperty("allCase")
    @Schema(type = "Long", example = "1")
    @NotNull(message = "Tổng tiết không được để trống")
    private Long allCase;


    @JsonProperty("purposeUse")
    @Schema(type = "String", example = "can be blank")
    @Size(max = 255, message = "Ghi chú không được vượt quá 255 ký tự")
    private String purposeUse;
}
