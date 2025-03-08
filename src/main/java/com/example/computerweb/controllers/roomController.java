package com.example.computerweb.controllers;

import com.example.computerweb.DTO.reponseBody.ResponseData;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Room controller only for role CSVC")
public class roomController {

    @GetMapping("/roomManagement")
    public ResponseData<?> getRoomManagement (){


        return null;
    }
}
