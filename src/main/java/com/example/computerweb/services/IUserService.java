package com.example.computerweb.services;


import com.example.computerweb.DTO.dto.userResponse.ProfileResponseDto;
import com.example.computerweb.DTO.dto.userResponse.UserResponseDto;
import com.example.computerweb.DTO.dto.userResponse.UserCreateMgnDto;
import com.example.computerweb.DTO.dto.userResponse.UserManagementDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.requestBody.accessRequest.UserLoginDto;
import com.example.computerweb.DTO.requestBody.userRequest.UserMngProfileRequestDto;
import com.example.computerweb.DTO.requestBody.userRequest.UserProfileRequestDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserRegisterDto;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface IUserService {

    ResponseEntity<String> handleRegister (UserRegisterDto userRegisterDTO);




    ResponseEntity<String> handleLogin (UserLoginDto userLoginDTO);

    ResponseEntity<String> handleLogout ();
    ResponseEntity<String> handleUpdateFieldProfile (UserProfileRequestDto userProfileDto);
    Map<String,String> handleGetDataUserCurrent ();

    UserResponseDto handleGetDataProfile ();

   List<UserManagementDto>  handleGetAllDataUser ();

   ResponseEntity<String> handleSaveProfileMng (UserMngProfileRequestDto userMngProfileRequestDto);

    Map<String, String> handleGetAllUserByRole ();

    UserCreateMgnDto handleGetDataForUserCreate ();

    ProfileResponseDto handleGetDataByUserMngUpdate(Long idUser);

    ResponseEntity<String> handleCheckExistEmailAndSendMail ( String email) throws MessagingException, UnsupportedEncodingException;

    ResponseData<?> handleUploadAvatar (MultipartFile file);
}
