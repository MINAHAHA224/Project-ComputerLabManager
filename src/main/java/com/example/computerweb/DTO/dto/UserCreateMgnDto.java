package com.example.computerweb.DTO.dto;

import com.example.computerweb.models.entity.MajorEntity;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCreateMgnDto {
    private Map<String,String> majors ;
    private Map<String , String> gender;
}
