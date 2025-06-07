package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.roomResponse.RoomManagementDto;
import com.example.computerweb.DTO.dto.roomResponse.RoomUpdateRpDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseFailure;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.roomRequest.RoomCreateRqDto;
import com.example.computerweb.DTO.requestBody.roomRequest.RoomUpdateRqDto;
import com.example.computerweb.exceptions.AuthenticationException;
import com.example.computerweb.exceptions.DataConflictException;
import com.example.computerweb.exceptions.DataNotFoundException;
import com.example.computerweb.models.entity.AccountEntity;
import com.example.computerweb.models.entity.FacilityEntity;
import com.example.computerweb.models.entity.RoomEntity;
import com.example.computerweb.repositories.IAccountRepository;
import com.example.computerweb.repositories.IFacilityRepository;
import com.example.computerweb.repositories.IRoomRepository;
import com.example.computerweb.services.IRoomService;
import com.example.computerweb.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl  implements IRoomService {
    private final IRoomRepository iRoomRepository;
    private final IFacilityRepository iFacilityRepository;
    private  final IAccountRepository iAccountRepository;


    @Override
    public ResponseData<?> handleGetAllDateRoom() {
        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity account = iAccountRepository.findAccountEntityByEmail(emailCurrentUser)
                .orElseThrow(() -> new AuthenticationException("Lỗi xác thực: Không tìm thấy người dùng với email: " + emailCurrentUser));


        List<RoomEntity> roomEntities = this.iRoomRepository.findAll();

        List<RoomManagementDto> data = new ArrayList<>();
        for ( RoomEntity roomEntity : roomEntities){
            RoomManagementDto roomManagementDto = new RoomManagementDto();
            roomManagementDto.setId(roomEntity.getId());
            roomManagementDto.setNameRoom(roomEntity.getNameRoom());
            roomManagementDto.setNumberOfComputers(roomEntity.getNumberOfComputers());
            roomManagementDto.setNumberOfComputerActive(roomEntity.getNumberOfComputerActive());
            roomManagementDto.setFacility(roomEntity.getFacility().getNameFacility());
            data.add(roomManagementDto);
        }

      return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công" , data) ;


    }

    @Override
    @Transactional
    public ResponseData<?> handleCreateRoom(RoomCreateRqDto roomCreateRqDto) {
        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity account = iAccountRepository.findAccountEntityByEmail(emailCurrentUser)
                .orElseThrow(() -> new AuthenticationException("Lỗi xác thực: Không tìm thấy người dùng với email: " + emailCurrentUser));


        FacilityEntity facility = this.iFacilityRepository.findFacilityEntityById(roomCreateRqDto.getFacility());
        if(facility == null ){
            throw new DataNotFoundException("Không tìm thấy cơ sở với ID:  " + roomCreateRqDto.getFacility());
        }

        boolean checkExistNameRoom = this.iRoomRepository.existsByNameRoom(roomCreateRqDto.getNameRoom().trim());
        if (checkExistNameRoom ){
            throw new DataConflictException("Tên phòng bị trùng lặp");
        }
            RoomEntity room = new RoomEntity();
            room.setNameRoom(roomCreateRqDto.getNameRoom().trim());
            room.setNumberOfComputers(roomCreateRqDto.getNumberOfComputer());
            room.setNumberOfComputerActive(roomCreateRqDto.getNumberOfComputerActive());
            room.setFacility(facility);

            this.iRoomRepository.save(room);

            return new ResponseSuccess<>(HttpStatus.OK.value(), "Tạo phòng thành công");
    }

    @Override
    public ResponseData<?> handleGetUpdateRoom(Long idRoom) {
        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity account = iAccountRepository.findAccountEntityByEmail(emailCurrentUser)
                .orElseThrow(() -> new AuthenticationException("Lỗi xác thực: Không tìm thấy người dùng với email: " + emailCurrentUser));

        RoomEntity room = this.iRoomRepository.findRoomEntityById(idRoom);
        if (room == null ){
            throw  new DataNotFoundException("Không tìm thấy phòng ID:  " + idRoom);
        }
        RoomUpdateRpDto result = new RoomUpdateRpDto();
        result.setIdRoom(room.getId());
        result.setNameRoom(room.getNameRoom().trim());
        result.setNumberOfComputer(room.getNumberOfComputers());
        result.setNumberOfComputerActive(room.getNumberOfComputerActive());
        result.setFacility(room.getFacility().getId());

        return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công" ,result );
    }

    @Override
    @Transactional
    public ResponseData<?> handlePostUpdateRoom(RoomUpdateRqDto roomUpdateRqDto) {

        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity account = iAccountRepository.findAccountEntityByEmail(emailCurrentUser)
                .orElseThrow(() -> new AuthenticationException("Lỗi xác thực: Không tìm thấy người dùng với email: " + emailCurrentUser));

        RoomEntity roomCurrent = this.iRoomRepository.findRoomEntityById(roomUpdateRqDto.getIdRoom());
        if (roomCurrent == null ){
            throw  new DataNotFoundException("Không tìm thấy phòng ID:  " + roomUpdateRqDto.getIdRoom());
        }

        FacilityEntity facility = this.iFacilityRepository.findFacilityEntityById(roomUpdateRqDto.getFacility());
        if (facility == null ){
            throw  new DataNotFoundException("Không tìm thấy cơ sở với ID:  " + roomUpdateRqDto.getFacility());
        }

        if ( !roomCurrent.getNameRoom().trim().equals(roomUpdateRqDto.getNameRoom().trim()) ){
            boolean checkNameUnique = this.iRoomRepository.existsByNameRoom(roomUpdateRqDto.getNameRoom().trim());
            if (checkNameUnique ){
                throw  new DataConflictException( "Tên phòng bị trùng lặp");
            }
        }
            roomCurrent.setNameRoom(roomUpdateRqDto.getNameRoom().trim());
            roomCurrent.setNumberOfComputers(roomUpdateRqDto.getNumberOfComputer());
            roomCurrent.setNumberOfComputerActive(roomUpdateRqDto.getNumberOfComputerActive());
            roomCurrent.setFacility(facility);
            this.iRoomRepository.save(roomCurrent);
            return new ResponseSuccess<>(HttpStatus.OK.value(), "Cập nhật phòng thành công");
    }

    @Override
    @Transactional
    public ResponseData<?> handleDeleteRoom(Long idRoom) {

        if (idRoom == null) {
            throw new DataNotFoundException("Không tìm thấy phòng với ID = null ");
        }

        RoomEntity room = this.iRoomRepository.findById(idRoom)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy phòng với ID = " + idRoom));
        try {
            this.iRoomRepository.deleteById(idRoom);
            return new ResponseSuccess<>(HttpStatus.OK.value(), "Xóa phòng thành công");
        }catch (DataIntegrityViolationException e ){
            log.error("--ER handleDeleteRoom : {}" , e.getMessage(),e);
            throw new DataConflictException("Không thể xóa phòng này vì đang được sử dụng trong một lịch thực hành.");
        }

    }
}
