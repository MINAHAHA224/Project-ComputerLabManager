package com.example.computerweb.controllers;

import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseFailure;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.accessRequest.UserLoginDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserRegisterDto;
import com.example.computerweb.services.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Tag(name="Access User")
@RequiredArgsConstructor
@RequestMapping("/access")
public class AccessController {

    // Error code : 400 of Controller

    private final  IUserService iUserService;
    @Operation(summary = "Register User" , description = "API Register user")
    @PostMapping("/register")
    public ResponseData<?> registerUser(@Valid @RequestBody UserRegisterDto userRegisterDTO) {
        ResponseEntity<String> handleSaveRegister = this.iUserService.handleRergister(userRegisterDTO);
        if (handleSaveRegister.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(),handleSaveRegister.getBody());
        }

        return new  ResponseSuccess<>(HttpStatus.OK.value(),handleSaveRegister.getBody());
    }

    @Operation(summary = "login User" , description = "API Login user")
    @PostMapping("/login")
    public ResponseData<String> createLogin (@Valid @RequestBody UserLoginDto userLoginDTO ){
      ResponseEntity<String> handleLogin = this.iUserService.handleLogin(userLoginDTO);
      if (handleLogin.getStatusCode() == HttpStatus.OK ){
          return new ResponseSuccess<>(HttpStatus.OK.value(), "Login Success" ,handleLogin.getBody() );

      }
      else {
          return new ResponseFailure(HttpStatus.BAD_REQUEST.value(),handleLogin.getBody());
      }

    }

}
