package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.userResponse.UserResponseDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.userRequest.UserProfileRequestDto;
import com.example.computerweb.services.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "UserController for GVU and Profile for GVU|GV|CSVC")
public class UserController {
    private  final IUserService iUserService;
    @Operation(summary = "Get profile of user" , description = "", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/profile")
    public ResponseData<UserResponseDto> getProfile (){
        UserResponseDto profileResponseDto = this.iUserService.handleGetDataProfile();
        return new ResponseSuccess<>(HttpStatus.OK.value() , "Thực hiện thành công" , profileResponseDto);
    }
    @Operation(summary = "Post profile of user" , description = "just post some field , Not post all field", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/profile")
    public  ResponseData<?> postProfile (@RequestBody UserProfileRequestDto userProfileDto){
        ResponseEntity<String> handleUpdateProfile = this.iUserService.handleUpdateFieldProfile(userProfileDto);
        return new ResponseSuccess<>(HttpStatus.OK.value(),handleUpdateProfile.getBody());
    }
//    @Operation(summary = "Show all information of User" , description = "Only for GVU", security = @SecurityRequirement(name = "bearerAuth"))
//    @GetMapping("/userManagement")
//    public ResponseData<List<UserManagementDto>> getUserManagement (){
//        List<UserManagementDto> user = this.iUserService.handleGetAllDataUser();
//        return new  ResponseSuccess<>(HttpStatus.OK.value(), "Execute success" ,user );
//    }
//
//    @Operation(summary = "update user" , security = @SecurityRequirement(name = "bearerAuth"))
//    @GetMapping("/userManagement/update/{userId}")
//    public ResponseData<ProfileResponseDto> updateUserManagement (@PathVariable("userId") Long userId){
//        ProfileResponseDto profileResponseDto = this.iUserService.handleGetDataByUserMngUpdate(userId);
//        return new ResponseSuccess<>(HttpStatus.OK.value() , "Execute Success" ,profileResponseDto );
//    }
//
//    @PostMapping("/userManagement/update")
//    @Operation(summary = "Post information of user" , description = "Only for GVU", security = @SecurityRequirement(name = "bearerAuth"))
//    public  ResponseData<?> postUserManagement (@RequestBody UserMngProfileRequestDto userMngRequestDto){
//        ResponseEntity<String> handleSaveProfile =  this.iUserService.handleSaveProfileMng(userMngRequestDto);
//        return new ResponseSuccess<>(HttpStatus.OK.value(), handleSaveProfile.getBody());
//    }
//
//    @Operation(summary = "create user " , security = @SecurityRequirement(name = "bearerAuth"))
//    @GetMapping("/userManagement/create")
//    public ResponseData<?> getRegisterUser (){
//        UserCreateMgnDto userCreateMgnDto = this.iUserService.handleGetDataForUserCreate();
//        return new ResponseSuccess<>(HttpStatus.OK.value(), "Execute success" , userCreateMgnDto);
//    }
//
//
//
//    @Operation(summary = "Register User" , description = "API Register user", security = @SecurityRequirement(name = "bearerAuth"))
//    @PostMapping("/userManagement/create")
//    public ResponseData<?> registerUser(@Valid @RequestBody UserRegisterDto userRegisterDTO) {
//        ResponseEntity<String> handleSaveRegister = this.iUserService.handleRegister(userRegisterDTO);
//        if (handleSaveRegister.getStatusCode() == HttpStatus.BAD_REQUEST) {
//            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(),handleSaveRegister.getBody());
//        }
//
//        return new  ResponseSuccess<>(HttpStatus.OK.value(),handleSaveRegister.getBody());
//    }



//    @GetMapping("/userManagement/export")
//    public  ResponseData<?> getExportUserDate ( ){
//
//
//        return null;
//    }
}
