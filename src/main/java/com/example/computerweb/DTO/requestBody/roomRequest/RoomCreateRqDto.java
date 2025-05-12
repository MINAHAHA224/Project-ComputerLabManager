package com.example.computerweb.DTO.requestBody.roomRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Validated
public class RoomCreateRqDto {

    @JsonProperty("nameRoom")
    @NotBlank(message = "NameRoom must not be blank")
    @Size(min = 2, max = 10, message = "NameRoom must be between 2 and 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "NameRoom must only contain uppercase letters and numbers")
    @Schema(type = "String", example = "2E309")
    private String nameRoom;

    @JsonProperty("facility")
    @NotNull(message = "Facility must not be null")
    @Schema(type = "Long", example = "1")
    private Long facility;

    @JsonProperty("numberOfComputer")
    @NotNull(message = "NumberOfComputer must not be null")
    @Min(value = 1, message = "NumberOfComputer must be at least 1")
    @Max(value = 1000, message = "NumberOfComputer cannot exceed 1000")
    @Schema(type = "Long", example = "35")
    private Long numberOfComputer;

    @JsonProperty("numberOfComputerError")
    @NotNull(message = "NumberOfComputerError must not be null")
    @Min(value = 0, message = "NumberOfComputerError must be at least 0")
    @Max(value = 1000, message = "NumberOfComputerError cannot exceed 1000")
    @Schema(type = "Long", example = "10")
    private Long numberOfComputerError;

}
