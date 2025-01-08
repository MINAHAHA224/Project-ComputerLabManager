package com.example.computerweb.controllers;

import com.example.computerweb.DTO.UserLoginDTO;
import com.example.computerweb.DTO.UserRegisterDTO;
import com.example.computerweb.services.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO, BindingResult bindingResult) {
        List<FieldError> errors = bindingResult.getFieldErrors();
        HashMap<String, String> errorMessages = new HashMap<>();
        for (FieldError error : errors) {
            System.out.println(">>>>> " + error.getField() + "-----" + error.getDefaultMessage());
            errorMessages.put(error.getField(), error.getDefaultMessage());
        }
        if (bindingResult.hasErrors()) {

            return ResponseEntity.badRequest().body(errorMessages);
        }

        ResponseEntity<String> handleSaveRegister = this.userService.handleRergister(userRegisterDTO);
        if (handleSaveRegister.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return ResponseEntity.badRequest().body("Failed Register");
        }

        return ResponseEntity.ok().body("Success Register");
    }


    @PostMapping("/login")
    public ResponseEntity<?> createLogin (@Valid @RequestBody UserLoginDTO userLoginDTO ){


      ResponseEntity<String> handleLogin = this.userService.handleLogin(userLoginDTO);
      if (handleLogin.getStatusCode() == HttpStatus.OK ){
          return ResponseEntity.ok().body(handleLogin.getBody());
      }
      else {
          return ResponseEntity.badRequest().body(handleLogin.getBody());
      }

    }

}
