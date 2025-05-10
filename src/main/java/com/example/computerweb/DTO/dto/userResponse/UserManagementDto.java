package com.example.computerweb.DTO.dto.userResponse;

import com.example.computerweb.models.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserManagementDto {


    private String id ;
    private String codeUser;

    private String firstName;

    private String lastName;

    private String gender;

    private String dateOfBirth ;

    private String phone ;

    private String email;

    private String passWord;

    private String informationCode;

    private String major;

    private String address;

    private String emailPersonal;

    private String province;

    private String district;

    private String ward;

    private String avatar;
}
