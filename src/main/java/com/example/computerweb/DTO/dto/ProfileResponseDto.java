package com.example.computerweb.DTO.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProfileResponseDto {
    private UserResponseDto dataUser;
    private UserCreateMgnDto dataBase ;
}
