package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.DTO.dto.TicketResponseMgmDto;
import com.example.computerweb.models.entity.PracticeCaseEntity;
import com.example.computerweb.models.entity.RoomEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.IPracticeCaseRepository;
import com.example.computerweb.repositories.IRoomRepository;
import com.example.computerweb.repositories.IUserRepository;
import com.example.computerweb.repositories.custom.TicketRequestCustom;
import com.example.computerweb.utils.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
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
    private final IPracticeCaseRepository iPracticeCaseRepository;
    private final IRoomRepository iRoomRepository;
    private  final IUserRepository iUserRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TicketResponseMgmDto> getAllDataRequestManagement() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        UserEntity userEntity = this.iUserRepository.findUserEntityByEmail(SecurityUtils.getPrincipal()).get();
        String sql = null;
        if ( userEntity.getRole().getNameRole().equals("GVU")){
            sql = "SELECT \n" +
                    "    PhieuYeuCau.YeuCauID AS 'Id', \n" +
                    "    PhieuYeuCau.NgayGui AS 'NgayGui', \n" +
                    "    NguoiDung.Ho + ' ' + NguoiDung.Ten AS 'GiangVien',\n" +
                    "    LoaiYC.NDloaiYC AS 'YeuCau',  \n" +
                    "    PhieuYeuCau.GhiChu AS 'GhiChu',\n" +
                    "    PhieuYeuCau.NgayCu AS 'NgayCu',\n" +
                    "    PhieuYeuCau.NgayMoi AS 'NgayMoi', \n" +
                    "\t ISNULL(CaCuID_FK, '') AS 'CaCu',\n" +
                    "    CaMoiID_FK AS 'CaMoi',\n" +
                    "   ISNULL(PhongIDCu_FK,'') AS 'PhongCu', \n" +
                    "   PhongIDMoi_FK AS 'PhongMoi', \n" +
                    "    ISNULL(Lop.TenLop ,'') AS 'TenLop', \n" +
                    "    ISNULL(MonHoc.TenMH,'') AS 'TenMonHoc',\n" +
                    "    TrangThai.TenTrangThai AS 'TrangThai'\n" +
                    "FROM PhieuYeuCau\n" +
                    "LEFT JOIN LoaiYC ON LoaiYC.LoaiYcID = PhieuYeuCau.LoaiYcID_FK\n" +
                    "LEFT JOIN NguoiDung ON NguoiDung.UserID = PhieuYeuCau.UserID_FK\n" +
                    "LEFT JOIN Lop ON Lop.LopID = PhieuYeuCau.LopID_FK\n" +
                    "LEFT JOIN MonHoc ON MonHoc.MonHocID = PhieuYeuCau.MonHocID_FK\n" +
                    "LEFT JOIN TrangThai  ON TrangThai.TrangThaiID = PhieuYeuCau.TrangThaiID_FK\n" +
                    "WHERE 1=1 AND PhieuYeuCau.DuyetCSVC = 2 AND PhieuYeuCau.DuyetGVU = 1 ";
        }else if ( userEntity.getRole().getNameRole().equals("CSVC")) {
            sql = "SELECT \n" +
                    "    PhieuYeuCau.YeuCauID AS 'Id', \n" +
                    "    PhieuYeuCau.NgayGui AS 'NgayGui', \n" +
                    "    NguoiDung.Ho + ' ' + NguoiDung.Ten AS 'GiangVien',\n" +
                    "    LoaiYC.NDloaiYC AS 'YeuCau',  \n" +
                    "    PhieuYeuCau.GhiChu AS 'GhiChu',\n" +
                    "    PhieuYeuCau.NgayCu AS 'NgayCu',\n" +
                    "    PhieuYeuCau.NgayMoi AS 'NgayMoi', \n" +
                    "\t ISNULL(CaCuID_FK, '') AS 'CaCu',\n" +
                    "    CaMoiID_FK AS 'CaMoi',\n" +
                    "   ISNULL(PhongIDCu_FK,'') AS 'PhongCu', \n" +
                    "   PhongIDMoi_FK AS 'PhongMoi', \n" +
                    "    ISNULL(Lop.TenLop ,'') AS 'TenLop', \n" +
                    "    ISNULL(MonHoc.TenMH,'') AS 'TenMonHoc',\n" +
                    "    TrangThai.TenTrangThai AS 'TrangThai'\n" +
                    "FROM PhieuYeuCau\n" +
                    "LEFT JOIN LoaiYC ON LoaiYC.LoaiYcID = PhieuYeuCau.LoaiYcID_FK\n" +
                    "LEFT JOIN NguoiDung ON NguoiDung.UserID = PhieuYeuCau.UserID_FK\n" +
                    "LEFT JOIN Lop ON Lop.LopID = PhieuYeuCau.LopID_FK\n" +
                    "LEFT JOIN MonHoc ON MonHoc.MonHocID = PhieuYeuCau.MonHocID_FK\n" +
                    "LEFT JOIN TrangThai  ON TrangThai.TrangThaiID = PhieuYeuCau.TrangThaiID_FK\n" +
                    "WHERE 1=1 AND PhieuYeuCau.DuyetCSVC = 1 AND PhieuYeuCau.DuyetGVU = 1 ";
        }


        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        List<TicketResponseMgmDto> datas = new ArrayList<>();


            for (Object[] result : results) {
                TicketResponseMgmDto ticketRequestDto = new TicketResponseMgmDto();
                ticketRequestDto.setIdTicket(result[0].toString());
                //    LocalDateTime dateTime = LocalDateTime.parse((result[1]).toString(), dateTimeFormatter);
                ticketRequestDto.setDateSent((dateTimeFormatter.format(((Timestamp) result[1]).toLocalDateTime())));
                ticketRequestDto.setTeacher(result[2].toString());
                ticketRequestDto.setTypeRequest(result[3].toString());
                ticketRequestDto.setNoteTicket(result[4].toString());
                if ((result[5]) == null) {
                    ticketRequestDto.setDateOld("");

                } else {
                    ticketRequestDto.setDateOld(dateFormat.format((Date) result[5]));
                }

                ticketRequestDto.setDateNew(dateFormat.format((Date) result[6]));
                // PracticeCaseOld
                StringBuilder practiceCaseOld = new StringBuilder();
                if (result[7].toString().isEmpty()) {
                    ticketRequestDto.setPracticeCaseOld(result[7].toString());
                } else if (result[7].toString().contains(",")) {
                    String[] loopPracticeCaseOlds = result[7].toString().split(",");
                    for (String loopPracticeCaseOld : loopPracticeCaseOlds) {
                        PracticeCaseEntity practiceCaseEntity = this.iPracticeCaseRepository.findPracticeCaseEntityById(Long.valueOf(loopPracticeCaseOld));
                        practiceCaseOld.append(practiceCaseEntity.getNamePracticeCase()).append(" ");
                    }
                    ticketRequestDto.setPracticeCaseOld(practiceCaseOld.toString());
                } else {
                    PracticeCaseEntity practiceCaseEntity = this.iPracticeCaseRepository.findPracticeCaseEntityById(Long.valueOf(result[7].toString()));
                    practiceCaseOld.append(practiceCaseEntity.getNamePracticeCase());
                    ticketRequestDto.setPracticeCaseOld(practiceCaseOld.toString());
                }


                // PracticeCaseNew
                StringBuilder practiceCaseNew = new StringBuilder();
                if (result[8].toString().contains(",")) {
                    String[] loopPracticeCaseNews = result[8].toString().split(",");

                    for (String loopPracticeCaseNew : loopPracticeCaseNews) {
                        PracticeCaseEntity practiceCaseEntity = this.iPracticeCaseRepository.findPracticeCaseEntityById(Long.valueOf(loopPracticeCaseNew));
                        practiceCaseNew.append(practiceCaseEntity.getNamePracticeCase()).append(" ");
                    }
                } else {
                    PracticeCaseEntity practiceCaseEntity = this.iPracticeCaseRepository.findPracticeCaseEntityById(Long.valueOf(result[8].toString()));
                    practiceCaseNew.append(practiceCaseEntity.getNamePracticeCase());
                }
                ticketRequestDto.setPracticeCaseNew(practiceCaseNew.toString());
//            ticketRequestDto.setPracticeCaseOld(result[7].toString());
//            ticketRequestDto.setPracticeCaseNew(result[8].toString());
                // RoomOld
                StringBuilder roomOld = new StringBuilder();
                if (result[9].toString().isEmpty()) {
                    ticketRequestDto.setNameRoomOld(result[9].toString());
                } else if (result[9].toString().contains(",")) {
                    String[] loopRoomOlds = result[9].toString().split(",");

                    for (String loopRoomOld : loopRoomOlds) {
                        RoomEntity roomEntity = this.iRoomRepository.findRoomEntityById(Long.valueOf(loopRoomOld));
                        roomOld.append(roomEntity.getNameRoom()).append(" ");
                    }
                    ticketRequestDto.setNameRoomOld(roomOld.toString());
                } else {
                    RoomEntity roomEntity = this.iRoomRepository.findRoomEntityById(Long.valueOf(result[9].toString()));
                    roomOld.append(roomEntity.getNameRoom());
                    ticketRequestDto.setNameRoomOld(roomOld.toString());
                }
                // RoomNew
                StringBuilder roomNew = new StringBuilder();
                if (result[10].toString().contains(",")) {
                    String[] loopRoomNews = result[10].toString().split(",");

                    for (String loopRoomNew : loopRoomNews) {
                        RoomEntity roomEntity = this.iRoomRepository.findRoomEntityById(Long.valueOf(loopRoomNew));
                        roomNew.append(roomEntity.getNameRoom()).append(" ");
                    }
                } else {
                    RoomEntity roomEntity = this.iRoomRepository.findRoomEntityById(Long.valueOf(result[10].toString()));
                    roomNew.append(roomEntity.getNameRoom());
                }
                ticketRequestDto.setNameRoomNew(roomNew.toString());
//            ticketRequestDto.setNameRoom(result[9].toString());
                if ( userEntity.getRole().getNameRole().equals("GVU")){
                    ticketRequestDto.setNameClassroom(result[11].toString());
                    ticketRequestDto.setNameSubject(result[12].toString());
                }else if ( userEntity.getRole().getNameRole().equals("CSVC")){
                    ticketRequestDto.setNameClassroom("");
                    ticketRequestDto.setNameSubject("");
                }

                ticketRequestDto.setStatus(result[13].toString());
                datas.add(ticketRequestDto);
            }


        return datas;
    }
}
