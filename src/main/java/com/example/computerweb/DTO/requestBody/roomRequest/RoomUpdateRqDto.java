package com.example.computerweb.DTO.requestBody.roomRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoomUpdateRqDto {

    private Long idRoom;

    @JsonProperty("nameRoom")
    @NotBlank(message = "Tên phòng không được để trống")
    @Size(min = 2, max = 10, message = "Tên phòng phải có độ dài từ 2 đến 10 ký tự")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Tên phòng chỉ được chứa chữ in hoa và số")
    @Schema(type = "String", example = "2E309")
    private String nameRoom;

    @JsonProperty("facility")
    @NotNull(message = "Cơ sở không được để trống")
    @Schema(type = "Long", example = "1")
    private Long facility;

    @JsonProperty("numberOfComputer")
    @NotNull(message = "Số lượng máy tính không được để trống")
    @Min(value = 1, message = "Số lượng máy tính ít nhất phải là 1")
    @Max(value = 70, message = "Số lượng máy tính không được vượt quá 70")
    @Schema(type = "Long", example = "35")
    private Long numberOfComputer;

    @JsonProperty("numberOfComputerError")
    @NotNull(message = "Số lượng máy tính bị lỗi không được để trống")
    @Min(value = 0, message = "Số lượng máy tính bị lỗi ít nhất phải là 0")
    @Max(value = 70, message = "Số lượng máy tính bị lỗi không được vượt quá 70")
    @Schema(type = "Long", example = "10")
    private Long numberOfComputerError;

}
