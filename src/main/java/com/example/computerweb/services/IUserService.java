package com.example.computerweb.services;


import com.example.computerweb.DTO.dto.ProfileResponseDto;
import com.example.computerweb.DTO.dto.UserManagementDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserLoginDto;
import com.example.computerweb.DTO.requestBody.userRequest.UserMngProfileRequestDto;
import com.example.computerweb.DTO.requestBody.userRequest.UserProfileRequestDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserRegisterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface IUserService {

    ResponseEntity<String> handleRergister (UserRegisterDto userRegisterDTO);

    boolean checkEamilExist (String email);

    boolean checkPhoneExist ( String phone);

    ResponseEntity<String> handleLogin (UserLoginDto userLoginDTO);

    ResponseEntity<String> handleUpdateFieldProfile (UserProfileRequestDto userProfileDto);
    Map<String,String> handleGetDataUserCurrent ();

    ProfileResponseDto handleGetDataProfile ();

   List<UserManagementDto>  handleGetAllDataUser ();

   ResponseEntity<String> handleSaveProfileMng (UserMngProfileRequestDto userMngProfileRequestDto);

    Map<String, String> handleGetAllUserByRole ();
}
