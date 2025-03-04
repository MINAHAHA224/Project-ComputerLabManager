package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.ProfileResponseDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.services.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "UserController for GVU and Profile for GVU|GV|CSVC")
public class UserController {
    private  final IUserService iUserService;

    @GetMapping("/profile")
    public ResponseData<ProfileResponseDto> getProfile (){
        ProfileResponseDto profileResponseDto = this.iUserService.handleGetDataProfile();
        return new ResponseSuccess<>(HttpStatus.OK.value() , "Execute success" , profileResponseDto);
    }
}
