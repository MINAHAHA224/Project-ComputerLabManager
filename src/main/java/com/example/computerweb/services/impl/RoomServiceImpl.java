package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.RoomManagementDto;
import com.example.computerweb.models.entity.RoomEntity;
import com.example.computerweb.repositories.IRoomRepository;
import com.example.computerweb.services.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl  implements IRoomService {
    private final IRoomRepository iRoomRepository;
    @Override
    public List<RoomManagementDto> handleGetAllDateRoom() {
        List<RoomEntity> roomEntities = this.iRoomRepository.findAll();
        List<RoomManagementDto> data = new ArrayList<>();
        for ( RoomEntity roomEntity : roomEntities){
            RoomManagementDto roomManagementDto = new RoomManagementDto();
            roomManagementDto.setId(roomEntity.getId().toString());
            roomManagementDto.setNameRoom(roomEntity.getNameRoom());
            roomManagementDto.setNumberOfComputers(roomEntity.getNumberOfComputers().toString());
            data.add(roomManagementDto);
        }
        return data;
    }
}
