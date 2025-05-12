package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.DTO.dto.calendarResponse.CalendarManagementDto;
import com.example.computerweb.models.entity.AccountEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.IAccountRepository;
import com.example.computerweb.repositories.IUserRepository;
import com.example.computerweb.repositories.custom.CalendarRepositoryCustom;
import com.example.computerweb.utils.DateUtils;
import com.example.computerweb.utils.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CalendarRepositoryCustomImpl implements CalendarRepositoryCustom {

    private final IUserRepository iUserRepository;
    private  final IAccountRepository iAccountRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CalendarManagementDto> findAllCustom() {
        String email = SecurityUtils.getPrincipal();
        AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(email).get();
        UserEntity userCurrent = account.getUser();
        Long idUser = userCurrent.getId();

        String sql = null;
        if (userCurrent.getRole().getNameRole().equals("GVU") || userCurrent.getRole().getNameRole().equals("CSVC")) {
            sql = "SELECT \n" +
                    "    LTH.LichID, \n" +
                    "    ISNULL(LTC.LopTinChiID,'') AS LopTinChiID ,\n" +
                    "    ISNULL(LTH.UserIdMp_FK,'') AS UserIdMp_FK ,  \n" +
                    "    ISNULL(MH.MaMH,'')  AS MaMH , \n" +
                    "\tISNULL(LTC.SoTC,'') AS SoTC ,\n" +
                    "    ISNULL(MH.TenMH,'')  AS TenMH , \n" +
                    "    ISNULL(LTC.Nhom,'')   AS Nhom ,\n" +
                    "    ISNULL(LTH.ToHop,'')   AS ToHop , \n" +
                    "    PTH.TenPhong, \n" +
                    "    CS.MaCS, \n" +
                    "    LTH.Thu,\n" +
                    "    LTH.SoTiet, \n" +
                    "    LTH.SoTietBD_FK,\n" +
                    "    ISNULL(ND1.Ho + ' ' + ND1.Ten, ND2.Ho + ' ' + ND2.Ten) AS GiangVien,\n" +
                    "    LTH.GhiChu,\n" +
                    "    DATEADD(DAY, (LTH.Thu - CASE WHEN DATEPART(WEEKDAY, TuanHK.NgayBatDau) = 1 THEN 7 ELSE DATEPART(WEEKDAY, TuanHK.NgayBatDau) - 1 END), TuanHK.NgayBatDau) AS NgayCuThe,\n" +
                    "    TT.MaTrangThai \n" +
                    "FROM LichThucHanh LTH -- Thêm Alias LTH cho ngắn gọn\n" +
                    "LEFT JOIN TrangThai TT ON LTH.TrangThai_FK = TT.TrangThaiID \n" +
                    "LEFT JOIN LopTinChi LTC ON LTC.LopTinChiID = LTH.LopTinChiID_FK \n" +
                    "LEFT JOIN PhongThucHanh PTH ON PTH.PhongID = LTH.PhongID_FK \n" +
                    "LEFT JOIN TietThucHanh TTH ON TTH.TietID = LTH.SoTietBD_FK \n" +
                    "LEFT JOIN CoSo CS ON CS.CoSoID = PTH.CoSo_Fk \n" +

                    "LEFT JOIN MonHoc MH ON MH.MonHocID = LTC.MonHoc_FK\n" +
                    "LEFT JOIN NguoiDung ND1 ON ND1.UserID = LTH.UserIdMp_FK\n" +
                    "LEFT JOIN NguoiDung ND2 ON ND2.UserID = LTC.UserID_FK\n" +
                    "LEFT JOIN TuanHoc_KiHoc TuanHK ON TuanHK.TuanHoc_KiHoc_Id = LTH.TuanHoc_KiHoc_Id_FK ";

        } else if (userCurrent.getRole().getNameRole().equals("GV")) {
            sql = "SELECT \n" +
                    "    LTH.LichID, \n" +
                    "    ISNULL(LTC.LopTinChiID,'') AS LopTinChiID ,\n" +
                    "    ISNULL(LTH.UserIdMp_FK,'') AS UserIdMp_FK ,  \n" +
                    "    ISNULL(MH.MaMH,'')  AS MaMH , \n" +
                    "\tISNULL(LTC.SoTC,'') AS SoTC ,\n" +
                    "    ISNULL(MH.TenMH,'')  AS TenMH , \n" +
                    "    ISNULL(LTC.Nhom,'')   AS Nhom ,\n" +
                    "    ISNULL(LTH.ToHop,'')   AS ToHop , \n" +
                    "    PTH.TenPhong, \n" +
                    "    CS.MaCS, \n" +
                    "    LTH.Thu,\n" +
                    "    LTH.SoTiet, \n" +
                    "    LTH.SoTietBD_FK,\n" +
                    "    ISNULL(ND1.Ho + ' ' + ND1.Ten, ND2.Ho + ' ' + ND2.Ten) AS GiangVien,\n" +
                    "    LTH.GhiChu,\n" +
                    "    DATEADD(DAY, (LTH.Thu - CASE WHEN DATEPART(WEEKDAY, TuanHK.NgayBatDau) = 1 THEN 7 ELSE DATEPART(WEEKDAY, TuanHK.NgayBatDau) - 1 END), TuanHK.NgayBatDau) AS NgayCuThe,\n" +
                    "    TT.MaTrangThai \n" +
                    "FROM LichThucHanh LTH -- Thêm Alias LTH cho ngắn gọn\n" +
                    "LEFT JOIN TrangThai TT ON LTH.TrangThai_FK = TT.TrangThaiID \n" +
                    "LEFT JOIN LopTinChi LTC ON LTC.LopTinChiID = LTH.LopTinChiID_FK \n" +
                    "LEFT JOIN PhongThucHanh PTH ON PTH.PhongID = LTH.PhongID_FK \n" +
                    "LEFT JOIN TietThucHanh TTH ON TTH.TietID = LTH.SoTietBD_FK \n" +
                    "LEFT JOIN CoSo CS ON CS.CoSoID = PTH.CoSo_Fk \n" +
                    "LEFT JOIN MonHoc MH ON MH.MonHocID = LTC.MonHoc_FK\n" +
                    "LEFT JOIN NguoiDung ND1 ON ND1.UserID = LTH.UserIdMp_FK\n" +
                    "LEFT JOIN NguoiDung ND2 ON ND2.UserID = LTC.UserID_FK\n" +
                    "LEFT JOIN TuanHoc_KiHoc TuanHK ON TuanHK.TuanHoc_KiHoc_Id = LTH.TuanHoc_KiHoc_Id_FK\n" +
                    "WHERE ND1.UserID = "+idUser+" OR ND2.UserID = " + idUser;
        }


        Query query = entityManager.createNativeQuery(sql);
        List<CalendarManagementDto> datas = new ArrayList<>();

        List<Object[]> results = query.getResultList();
        if (!results.isEmpty() ){
            if(userCurrent.getRole().getNameRole().equals("GVU") ||
                    userCurrent.getRole().getNameRole().equals("GV")){
                for (Object[] result : results) {
                    CalendarManagementDto data = new CalendarManagementDto();
                    data.setCalendarId( result[0].toString());
                    data.setCreditClassId( String.valueOf(result[1]).equals("0") ? "" : String.valueOf(result[1]) );
                    data.setUserIdMp_FK(String.valueOf(result[2]).equals("0") ? "" : String.valueOf(result[2]));
                    data.setCodeSubject(result[3].toString());
                    data.setCredit(result[4].toString());
                    data.setNameSubject( result[5].toString());
                    data.setGroup( result[6].toString());
                    data.setCombination( result[7].toString());
                    data.setNameRoom( result[8].toString());
                    data.setCodeFacility( result[9].toString());
                    data.setDay( result[10].toString());
                    data.setLesson( result[11].toString());
                    data.setLessonBegin( result[12].toString());
                    data.setNameTeacher( result[13].toString());
                    data.setNote( result[14].toString());
                    data.setDate(DateUtils.convertToString((Date) result[15]) );
                    data.setStatusCalendar(result[16].toString());
                    datas.add(data);
                }
                return datas;
            }else if (userCurrent.getRole().getNameRole().equals("CSVC")){
                for (Object[] result : results) {
                    CalendarManagementDto data = new CalendarManagementDto();
                    data.setCalendarId( result[0].toString());
                    data.setCreditClassId( String.valueOf(result[1]).equals("0") ? "" : String.valueOf(result[1]) );
                    data.setUserIdMp_FK(String.valueOf(result[2]).equals("0") ? "" : String.valueOf(result[2]));
                    data.setCodeSubject("");
                    data.setCredit("");
                    data.setNameSubject("");
                    data.setGroup("");
                    data.setCombination("");

                    data.setNameRoom( result[8].toString());
                    data.setCodeFacility( result[9].toString());
                    data.setDay( result[10].toString());
                    data.setLesson( result[11].toString());
                    data.setLessonBegin( result[12].toString());
                    data.setNameTeacher(result[13].toString());
                    data.setNote( result[14].toString());
                    data.setDate( DateUtils.convertToString((Date) result[15]));
                    data.setStatusCalendar(result[16].toString());
                    datas.add(data);
                }
                return datas;
            }
        }

    return null;
    }
}
