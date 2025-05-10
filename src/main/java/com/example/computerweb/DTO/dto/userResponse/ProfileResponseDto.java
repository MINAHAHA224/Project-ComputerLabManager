package com.example.computerweb.DTO.dto.userResponse;

import com.example.computerweb.DTO.dto.userResponse.UserCreateMgnDto;
import com.example.computerweb.DTO.dto.userResponse.UserResponseDto;
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
