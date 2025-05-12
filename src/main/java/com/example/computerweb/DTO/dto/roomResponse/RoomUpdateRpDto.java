package com.example.computerweb.DTO.dto.roomResponse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoomUpdateRpDto {
    private Long  idRoom;
    private String nameRoom;
    private Long numberOfComputer;
    private Long numberOfComputerError;
    private Long facility;
}
