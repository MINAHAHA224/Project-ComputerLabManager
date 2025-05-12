package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.roomResponse.RoomManagementDto;
import com.example.computerweb.DTO.dto.roomResponse.RoomUpdateRpDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseFailure;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.roomRequest.RoomCreateRqDto;
import com.example.computerweb.DTO.requestBody.roomRequest.RoomUpdateRqDto;
import com.example.computerweb.models.entity.FacilityEntity;
import com.example.computerweb.models.entity.RoomEntity;
import com.example.computerweb.repositories.IFacilityRepository;
import com.example.computerweb.repositories.IRoomRepository;
import com.example.computerweb.services.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl  implements IRoomService {
    private final IRoomRepository iRoomRepository;
    private final IFacilityRepository iFacilityRepository;
    @Override
    public ResponseData<?> handleGetAllDateRoom() {
        List<RoomEntity> roomEntities = this.iRoomRepository.findAll();
        List<RoomManagementDto> data = new ArrayList<>();
        for ( RoomEntity roomEntity : roomEntities){
            RoomManagementDto roomManagementDto = new RoomManagementDto();
            roomManagementDto.setId(roomEntity.getId());
            roomManagementDto.setNameRoom(roomEntity.getNameRoom());
            roomManagementDto.setNumberOfComputers(roomEntity.getNumberOfComputers());
            roomManagementDto.setNumberOfComputerError(roomEntity.getNumberOfComputerError());
            roomManagementDto.setFacility(roomEntity.getFacility().getNameFacility());
            data.add(roomManagementDto);
        }
        if( data!=null && !data.isEmpty()){
            return new ResponseSuccess<>(HttpStatus.OK.value(), "Execute success" , data) ;
        }else {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "No data");
        }

    }

    @Override
    @Transactional
    public ResponseData<?> handleCreateRoom(RoomCreateRqDto roomCreateRqDto) {
        boolean checkExistNameRoom = this.iRoomRepository.existsByNameRoom(roomCreateRqDto.getNameRoom().trim());
        FacilityEntity facility = this.iFacilityRepository.findFacilityEntityById(roomCreateRqDto.getFacility());
        if (checkExistNameRoom ){
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(),  "Name room is duplicate");
        }
        RoomEntity room = new RoomEntity();
        try {
            room.setNameRoom(roomCreateRqDto.getNameRoom().trim());
            room.setNumberOfComputers(roomCreateRqDto.getNumberOfComputer());
            room.setNumberOfComputerError(roomCreateRqDto.getNumberOfComputerError());
            room.setFacility(facility);

            this.iRoomRepository.save(room);

            return new ResponseSuccess<>(HttpStatus.OK.value(), "Create room success");
        }catch (RuntimeException e){
            System.out.println("--ER : handleCreateRoom " +e.getMessage());
            e.printStackTrace();
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(),  "Create room fail");
        }

    }

    @Override
    public ResponseData<?> handleGetUpdateRoom(Long idRoom) {

        RoomEntity room = this.iRoomRepository.findRoomEntityById(idRoom);
        RoomUpdateRpDto result = new RoomUpdateRpDto();
        result.setIdRoom(room.getId());
        result.setNameRoom(room.getNameRoom().trim());
        result.setNumberOfComputer(room.getNumberOfComputers());
        result.setNumberOfComputerError(room.getNumberOfComputerError());
        result.setFacility(room.getFacility().getId());

        return new ResponseSuccess<>(HttpStatus.OK.value(), "Execute success" ,result );
    }

    @Override
    @Transactional
    public ResponseData<?> handlePostUpdateRoom(RoomUpdateRqDto roomUpdateRqDto) {

        RoomEntity roomCurrent = this.iRoomRepository.findRoomEntityById(roomUpdateRqDto.getIdRoom());
        FacilityEntity facility = this.iFacilityRepository.findFacilityEntityById(roomUpdateRqDto.getFacility());
        if ( !roomCurrent.getNameRoom().trim().equals(roomUpdateRqDto.getNameRoom().trim()) ){
            boolean checkNameUnique = this.iRoomRepository.existsByNameRoom(roomUpdateRqDto.getNameRoom().trim());
            if (checkNameUnique ){
                return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Name room is duplicated");
            }
        }
        try {
            roomCurrent.setNameRoom(roomUpdateRqDto.getNameRoom().trim());
            roomCurrent.setNumberOfComputers(roomUpdateRqDto.getNumberOfComputer());
            roomCurrent.setNumberOfComputerError(roomUpdateRqDto.getNumberOfComputerError());
            roomCurrent.setFacility(facility);
            this.iRoomRepository.save(roomCurrent);
            return new ResponseSuccess<>(HttpStatus.OK.value(), "Update room success");
        }catch (RuntimeException e){
            System.out.println("--ER handlePostUpdateRoom :" + e.getMessage());
            e.printStackTrace();
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Update room fail");
        }

    }

    @Override
    @Transactional
    public ResponseData<?> handleDeleteRoom(Long idRoom) {
        try {
            this.iRoomRepository.deleteById(idRoom);
            return new ResponseSuccess<>(HttpStatus.OK.value(), "Delete success");
        }catch (RuntimeException e){
            System.out.println("--ER handleDeleteRoom " + e.getMessage());
            e.printStackTrace();
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(),  "Delete fail");
        }

    }
}
