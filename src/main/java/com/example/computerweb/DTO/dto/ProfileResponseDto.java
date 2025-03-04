package com.example.computerweb.DTO.dto;

import com.example.computerweb.models.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProfileResponseDto {


    private String id ;
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
