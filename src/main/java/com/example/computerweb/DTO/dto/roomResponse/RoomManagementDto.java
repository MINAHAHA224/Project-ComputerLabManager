package com.example.computerweb.DTO.dto.roomResponse;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoomManagementDto {

    private Long id ;
    private  String nameRoom ;
    private Long numberOfComputers;
    private Long numberOfComputerError;
    private String facility;
}
