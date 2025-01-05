package com.example.computerweb.services;


import com.example.computerweb.DTO.UserRegisterDTO;
import com.example.computerweb.models.User;
import com.example.computerweb.services.Impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;

public interface IUserService  {

    ResponseEntity<String> handleRergister (UserRegisterDTO userRegisterDTO);
}
