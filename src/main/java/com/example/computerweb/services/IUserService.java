package com.example.computerweb.services;


import com.example.computerweb.DTO.UserLoginDTO;
import com.example.computerweb.DTO.UserRegisterDTO;
import com.example.computerweb.models.User;
import com.example.computerweb.services.Impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;

public interface IUserService  {

    ResponseEntity<String> handleRergister (UserRegisterDTO userRegisterDTO);

    boolean checkEamilExist (String email);

    ResponseEntity<String> handleLogin (UserLoginDTO userLoginDTO);
}
