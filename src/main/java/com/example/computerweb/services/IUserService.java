package com.example.computerweb.services;


import com.example.computerweb.DTO.requestBody.accessRequest.UserLoginDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserRegisterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface IUserService {

    ResponseEntity<String> handleRergister (UserRegisterDto userRegisterDTO);

    boolean checkEamilExist (String email);

    boolean checkPhoneExist ( String phone);

    ResponseEntity<String> handleLogin (UserLoginDto userLoginDTO);

    Map<String, String> handleGetAllUserByRole ();
}
