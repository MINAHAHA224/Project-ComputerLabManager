package com.example.computerweb.DTO.dto;

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

    private String id ;


    private  String nameRoom ;


    private String numberOfComputers;
}
