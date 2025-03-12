package com.example.computerweb.DTO.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserResponseDto {


    private String id ;
    private String userCode;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String major;
    private String dateOfBirth ;


    private String informationCode;
    private String phone ;
    private String emailPersonal;
    private String province;
    private String district;
    private String ward;
    private String address;

}
