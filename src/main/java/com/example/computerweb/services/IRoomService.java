package com.example.computerweb.services;

import com.example.computerweb.DTO.dto.RoomManagementDto;
import com.example.computerweb.models.entity.RoomEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IRoomService {
    List<RoomManagementDto> handleGetAllDateRoom ();
}
