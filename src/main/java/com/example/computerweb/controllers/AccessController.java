package com.example.computerweb.controllers;

import com.example.computerweb.DTO.dto.HomeResponseDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseFailure;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.accessRequest.UserLoginDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserRegisterDto;
import com.example.computerweb.services.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@Tag(name="Access user for GVU|CSVC|GV")
@RequiredArgsConstructor
public class AccessController {

    // Error code : 400 of Controller

    private final  IUserService iUserService;


    @Operation(summary = "login User" , description = "API Login user")
    @PostMapping("/access/login")
    public ResponseData<?> createLogin (@Valid @RequestBody UserLoginDto userLoginDTO ){
      ResponseEntity<String> handleLogin = this.iUserService.handleLogin(userLoginDTO);
      if (handleLogin.getStatusCode() == HttpStatus.OK ){
          Map<String,String> token = new HashMap<>();
          token.put("token" , handleLogin.getBody());
          return new ResponseSuccess<>(HttpStatus.OK.value(), "Login Success" ,token );

      }
      else {
          return new ResponseFailure(HttpStatus.BAD_REQUEST.value(),handleLogin.getBody());
      }

    }

    @Operation(summary = "Home page" , description = "Show Information and Role of user")
    @GetMapping("/home")
    public ResponseData<HomeResponseDto> getHomePage (){

        Map<String , String> dataUser = this.iUserService.handleGetDataUserCurrent();
        HomeResponseDto homeResponseDto = new HomeResponseDto();
        homeResponseDto.setDataUser(dataUser);
        return new ResponseData<>(HttpStatus.OK.value() , "Execute Success" , homeResponseDto );
    }



    @PostMapping("/access/forgotPassword")
    public ResponseData<?>  postForgotPassword (@RequestParam(required = true) String email) throws MessagingException, UnsupportedEncodingException {
        ResponseEntity<String> sendEmail = this.iUserService.handleCheckExistEmailAndSendMail(email);
                if(sendEmail.getStatusCode() ==HttpStatus.BAD_REQUEST){
                    return new ResponseFailure( HttpStatus.BAD_REQUEST.value(), sendEmail.getBody()) ;
                }
                else {
                    return new ResponseSuccess<>( HttpStatus.OK.value(), sendEmail.getBody()) ;
                }

    }
    // navigation to next Page

}
