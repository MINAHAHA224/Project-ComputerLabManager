package com.example.computerweb.services;

import com.example.computerweb.DTO.dto.roomResponse.RoomManagementDto;
import com.example.computerweb.DTO.dto.roomResponse.RoomUpdateRpDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.requestBody.roomRequest.RoomCreateRqDto;
import com.example.computerweb.DTO.requestBody.roomRequest.RoomUpdateRqDto;

import java.util.List;

public interface IRoomService {
    ResponseData<?> handleGetAllDateRoom ();

    ResponseData<?> handleCreateRoom (RoomCreateRqDto roomCreateRqDto);

    ResponseData<?> handleGetUpdateRoom (Long idRoom);

    ResponseData<?> handlePostUpdateRoom (RoomUpdateRqDto roomUpdateRpDto);

    ResponseData<?> handleDeleteRoom (Long idRoom);
}
