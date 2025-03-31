package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.DTO.dto.TicketResponseMgmDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRequestOneDto;
import com.example.computerweb.models.entity.*;
import com.example.computerweb.repositories.*;
import com.example.computerweb.repositories.custom.TicketRequestCustom;
import com.example.computerweb.utils.DateUtils;
import com.example.computerweb.utils.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.sql.Timestamp;

@Repository
@RequiredArgsConstructor
public class TicketRequestCustomImpl implements TicketRequestCustom {
    private final IUserRepository iUserRepository;
    private final IStatusRepository iStatusRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<TicketRequestOneDto> findListTicketRequestForCSVC() {
        String sql ="SELECT PhieuYeuCau.YeuCauID , PhieuYeuCau.LoaiYcID_FK , PhieuYeuCau.NgayGui , PhieuYeuCau.UserIdNguoiGui_FK , PhieuYeuCau.DuyetCSVC\n" +
                "FROM PhieuYeuCau\n" +
                "WHERE PhieuYeuCau.LoaiYcID_FK != 2";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> resultList = query.getResultList();
        List<TicketRequestOneDto> ticketRequestOneDtoList = new ArrayList<>();
        for ( Object[] result : resultList){
            TicketRequestOneDto ticketRequestOneDto = new TicketRequestOneDto();
            ticketRequestOneDto.setRequestId(result[0].toString());
            ticketRequestOneDto.setTypeRequestId(result[1].toString());
            ticketRequestOneDto.setDateRequest(DateUtils.dateTimeConvertToString((Timestamp)result[2]));
            UserEntity user = this.iUserRepository.findUserEntityById(Long.valueOf(result[3].toString()));
            ticketRequestOneDto.setUserRequest(user.getFirstName() + " " + user.getLastName());
            StatusEntity statusCSVC = this.iStatusRepository.findStatusEntityById(Long.valueOf(result[4].toString()));
            ticketRequestOneDto.setStatus(statusCSVC.getNameStatus());

            ticketRequestOneDtoList.add(ticketRequestOneDto);
        }
        return ticketRequestOneDtoList;
    }
}
