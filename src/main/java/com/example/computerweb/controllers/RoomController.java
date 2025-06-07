package com.example.computerweb.controllers;


import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.requestBody.roomRequest.RoomCreateRqDto;
import com.example.computerweb.DTO.requestBody.roomRequest.RoomUpdateRqDto;
import com.example.computerweb.services.IRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Room manager for ROLE CSVC")
public class RoomController {
    private final IRoomService iRoomService;


    @GetMapping(value = "/roomManagement")
    public ResponseData<?> getRoomManagement (){
        return this.iRoomService.handleGetAllDateRoom();
    }

//    @PostMapping (value = "/roomManagement/create")
//    public ResponseEntity<ResponseData<?>>  postCreateRoom (@Valid  @RequestBody RoomCreateRqDto roomCreateRqDto){
//        ResponseData<?> response = this.iRoomService.handleCreateRoom(roomCreateRqDto);
//        return  ResponseEntity.status(response.getStatus()).body(response);
//    }

    @PostMapping (value = "/roomManagement/create")
    public ResponseData<?>  postCreateRoom (@Valid  @RequestBody RoomCreateRqDto roomCreateRqDto){
        return  this.iRoomService.handleCreateRoom(roomCreateRqDto);
    }

    @GetMapping(value = "/roomManagement/update/{idRoom}")
    public ResponseData<?> getUpdateRoom (@PathVariable("idRoom") Long idRoom){
        return this.iRoomService.handleGetUpdateRoom(idRoom);
    }

    @PostMapping(value = "/roomManagement/update")
    public ResponseData<?> postUpdateRoom (@Valid @RequestBody RoomUpdateRqDto roomUpdateRqDto){
        return this.iRoomService.handlePostUpdateRoom(roomUpdateRqDto);
    }

    @DeleteMapping(value = "/roomManagement/delete/{idRoom}")
    public ResponseData<?> deleteRoom (@PathVariable("idRoom") Long idRoom){
        return this.iRoomService.handleDeleteRoom(idRoom);
    }

}
