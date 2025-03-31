package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.DTO.dto.CalendarManagementDto;
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
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
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
                    "    LichThucHanh.LichID, \n" +
                    "    ISNULL(LopTinChi.LopTinChiID,'') AS LopTinChiID ,\n" +
                    "    ISNULL(LichThucHanh.UserIdMp_FK,'') AS UserIdMp_FK ,  \n" +
                    "    ISNULL(MonHoc.MaMH,'')  AS MaMH ,  \n" +
                    "    ISNULL(MonHoc.TenMH,'')  AS TenMH , \n" +
                    "    ISNULL(LichThucHanh.Nhom,'')   AS Nhom ,\n" +
                    "\tISNULL(LichThucHanh.ToHop,'')   AS ToHop ,\n" +
                    "ISNULL( Lop.MaLop+'-'+LichThucHanh.Nhom ,'')  AS MaLop," +
                    "    PhongThucHanh.TenPhong, \n" +
                    "    CoSo.MaCS, \n" +
                    "    LichThucHanh.Thu,\n" +
                    "    LichThucHanh.SoTiet, \n" +
                    "    LichThucHanh.SoTietBD_FK,\n" +
                    "    ISNULL(ND1.Ho + ' ' + ND1.Ten, ND2.Ho + ' ' + ND2.Ten) AS GiangVien,\n" +
                    "    LichThucHanh.GhiChu,\n" +
                    "    DATEADD(DAY, (LichThucHanh.Thu - 2), TuanHoc_KiHoc.NgayBatDau) AS NgayCuThe," +
                    " TrangThai.MaTrangThai, "+
                    "LopTinChi.SoTC "+
                    " FROM LichThucHanh\n" +
                    "LEFT JOIN TrangThai ON LichThucHanh.TrangThai_FK = TrangThai.TrangThaiID "+
                    "LEFT JOIN LopTinChi ON LopTinChi.LopTinChiID = LichThucHanh.LopTinChiID_FK\n" +
                    "LEFT JOIN PhongThucHanh ON PhongThucHanh.PhongID = LichThucHanh.PhongID_FK\n" +
                    "LEFT JOIN TietThucHanh ON TietThucHanh.TietID = LichThucHanh.SoTietBD_FK\n" +
                    "LEFT JOIN CoSo ON CoSo.CoSoID = PhongThucHanh.CoSo_Fk\n" +
                    "LEFT JOIN Lop ON Lop.LopID = LopTinChi.Lop_FK\n" +
                    "LEFT JOIN MonHoc ON MonHoc.MonHocID = LopTinChi.MonHoc_FK\n" +
                    "LEFT JOIN NguoiDung ND1 ON ND1.UserID = LichThucHanh.UserIdMp_FK\n" +
                    "LEFT JOIN NguoiDung ND2 ON ND2.UserID = LopTinChi.UserID_FK\n" +
                    "LEFT JOIN TuanHoc_KiHoc ON TuanHoc_KiHoc.TuanHoc_KiHoc_Id = LichThucHanh.TuanHoc_KiHoc_Id_FK";

        } else if (userCurrent.getRole().getNameRole().equals("GV")) {
            sql = "SELECT \n" +
                    "    LichThucHanh.LichID, \n" +
                    "    ISNULL(LopTinChi.LopTinChiID,'') AS LopTinChiID ,\n" +
                    "    ISNULL(LichThucHanh.UserIdMp_FK,'') AS UserIdMp_FK ,  \n" +
                    "    ISNULL(MonHoc.MaMH,'')  AS MaMH ,  \n" +
                    "    ISNULL(MonHoc.TenMH,'')  AS TenMH , \n" +
                    "    ISNULL(LichThucHanh.Nhom,'')   AS Nhom ,\n" +
                    "\tISNULL(LichThucHanh.ToHop,'')   AS ToHop ,\n" +
                    " ISNULL( Lop.MaLop+'-'+LichThucHanh.Nhom ,'')  AS MaLop," +
                    "    PhongThucHanh.TenPhong, \n" +
                    "    CoSo.MaCS, \n" +
                    "    LichThucHanh.Thu,\n" +
                    "    LichThucHanh.SoTiet, \n" +
                    "    LichThucHanh.SoTietBD_FK,\n" +
                    "    ISNULL(ND1.Ho + ' ' + ND1.Ten, ND2.Ho + ' ' + ND2.Ten) AS GiangVien,\n" +
                    "    LichThucHanh.GhiChu,\n" +
                    "    DATEADD(DAY, (LichThucHanh.Thu - 2), TuanHoc_KiHoc.NgayBatDau) AS NgayCuThe, " +
                    " TrangThai.MaTrangThai , "+
                    "LopTinChi.SoTC "+
                    " FROM LichThucHanh\n" +
                    "LEFT JOIN TrangThai ON LichThucHanh.TrangThai_FK = TrangThai.TrangThaiID "+
                    "LEFT JOIN LopTinChi ON LopTinChi.LopTinChiID = LichThucHanh.LopTinChiID_FK\n" +
                    "LEFT JOIN PhongThucHanh ON PhongThucHanh.PhongID = LichThucHanh.PhongID_FK\n" +
                    "LEFT JOIN TietThucHanh ON TietThucHanh.TietID = LichThucHanh.SoTietBD_FK\n" +
                    "LEFT JOIN CoSo ON CoSo.CoSoID = PhongThucHanh.CoSo_Fk\n" +
                    "LEFT JOIN Lop ON Lop.LopID = LopTinChi.Lop_FK\n" +
                    "LEFT JOIN MonHoc ON MonHoc.MonHocID = LopTinChi.MonHoc_FK\n" +
                    "LEFT JOIN NguoiDung ND1 ON ND1.UserID = LichThucHanh.UserIdMp_FK\n" +
                    "LEFT JOIN NguoiDung ND2 ON ND2.UserID = LopTinChi.UserID_FK\n" +
                    "LEFT JOIN TuanHoc_KiHoc ON TuanHoc_KiHoc.TuanHoc_KiHoc_Id = LichThucHanh.TuanHoc_KiHoc_Id_FK\n" +
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
                    data.setNameSubject( result[4].toString());
                    data.setGroup( result[5].toString());
                    data.setCombination( result[6].toString());
                    data.setCodeClassroom( result[7].toString());
                    data.setNameRoom( result[8].toString());
                    data.setCodeFacility( result[9].toString());
                    data.setDay( result[10].toString());
                    data.setLesson( result[11].toString());
                    data.setLessonBegin( result[12].toString());
                    data.setNameTeacher( result[13].toString());
                    data.setNote( result[14].toString());
                    data.setDate(DateUtils.convertToString((Date) result[15]) );
                    data.setStatusCalendar(result[16].toString());
                    data.setCredit(result[17] != null ? result[17] .toString() : null);
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
                    data.setNameSubject("");
                    data.setGroup("");
                    data.setCombination("");
                    data.setCodeClassroom("");
                    data.setNameRoom( result[8].toString());
                    data.setCodeFacility( result[9].toString());
                    data.setDay( result[10].toString());
                    data.setLesson( result[11].toString());
                    data.setLessonBegin( result[12].toString());
                    data.setNameTeacher(result[13].toString());
                    data.setNote( result[14].toString());
                    data.setDate( DateUtils.convertToString((Date) result[15]));
                    data.setStatusCalendar(result[16].toString());
                    data.setCredit(result[17] != null ? result[17] .toString() : null);
                    datas.add(data);
                }
                return datas;
            }
        }

    return null;
    }
}
