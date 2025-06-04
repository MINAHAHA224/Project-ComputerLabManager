package com.example.computerweb.DTO.requestBody.calendarRequest;

import com.example.computerweb.models.enums.PurposeUse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Validated
public class CalendarRequestDto { // Đổi tên cho phù hợp nếu đây là DTO mới hoàn toàn
    @JsonProperty("creditClassId")
    @Schema(type = "Long",  example = "1")
    @NotNull(message = "Lớp tín chỉ không được để trống")
    private Long creditClassId;

    @JsonProperty("idFacility")
    @Schema(type = "Long",  example = "1")
    @NotNull(message = "Cơ sở không được để trống")
    private Long idFacility;

    @JsonProperty("weekSemesterId") // Tuần bắt đầu cho buổi học ĐẦU TIÊN
    @Schema(type = "Long", example = "13")
    @NotNull(message = "Tuần học bắt đầu không được để trống")
    private Long startWeekSemesterId; // Đổi tên để rõ nghĩa hơn

    @JsonProperty("dayId") // Thứ trong tuần
    @Schema(type = "Long", example = "2") // Ví dụ: 2 là Thứ Hai
    @NotNull(message = "Thứ trong tuần không được để trống")
    private Long dayId;

    @JsonProperty("practiceCaseBeginId") // Tiết bắt đầu trong ngày
    @Schema(type = "Long", example = "7")
    @NotNull(message = "Tiết bắt đầu không được để trống")
    private Long practiceCaseBeginId;

    @JsonProperty("allCase") // Số tiết mỗi buổi học
    @Schema(type = "Long", example = "2")
    @NotNull(message = "Tổng số tiết mỗi buổi không được để trống")
    private Long allCasePerSession; // Đổi tên để rõ nghĩa

    @JsonProperty("purposeUse")
    @Schema(type = "String", example = "Lập trình C căn bản")
    @Size(max = 255, message = "Ghi chú không được vượt quá 255 ký tự")
    private String purposeUse;

    // TÙY CHỌN: Nếu muốn người dùng chỉ định số nhóm khi tạo tự động
    // @JsonProperty("numberOfGroups")
    // @Schema(type = "Integer", example = "2", description = "Số nhóm cần tạo cho lớp tín chỉ này. Nếu bỏ qua, hệ thống có thể tự xác định.")
    // @Positive(message = "Số nhóm phải là số dương")
    // private Integer numberOfGroupsToCreate;
}




