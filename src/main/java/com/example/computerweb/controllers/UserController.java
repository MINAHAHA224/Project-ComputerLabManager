package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.ProfileResponseDto;
import com.example.computerweb.DTO.dto.UserManagementDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.userRequest.UserMngRequestDto;
import com.example.computerweb.DTO.requestBody.userRequest.UserProfileRequestDto;
import com.example.computerweb.services.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PostMapping("/profile")
    public  ResponseData<?> postProfile (@RequestBody UserProfileRequestDto userProfileDto){
        ResponseEntity<String> handleUpdateProfile = this.iUserService.handleUpdateFieldProfile(userProfileDto);
        return new ResponseSuccess<>(HttpStatus.OK.value(),handleUpdateProfile.getBody());
    }

    @GetMapping("/userManagement")
    public ResponseData<List<UserManagementDto>> getUserManagement (){
        List<UserManagementDto> user = this.iUserService.handleGetAllDataUser();
        return new  ResponseSuccess<>(HttpStatus.OK.value(), "Execute success" ,user );
    }

    @PostMapping("/userManagement")
    public  ResponseData<?> postUserManagement (@RequestBody UserMngRequestDto userMngRequestDto){


        return null;
    }

    @GetMapping("/userManagement/export")
    public  ResponseData<?> getExportUserDate ( ){


        return null;
    }
}
