package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.DTO.dto.CalendarManagementDto;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.IUserRepository;
import com.example.computerweb.repositories.custom.CalendarRepositoryCustom;
import com.example.computerweb.utils.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CalendarRepositoryCustomImpl implements CalendarRepositoryCustom {

    private final IUserRepository iUserRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CalendarManagementDto> findAllCustom() {
        UserEntity userCurrent = this.iUserRepository.findUserEntityByEmail(SecurityUtils.getPrincipal()).get();
        Long idUser = userCurrent.getId();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String sql = null;
        if (userCurrent.getRole().getNameRole().equals("GVU") || userCurrent.getRole().getNameRole().equals("CSVC")) {
            sql = "SELECT  LichThucHanh.Ngay AS 'date' ,( NguoiDung.Ho + ' ' + NguoiDung.Ten) AS 'teacher' , PhongThucHanh.TenPhong AS 'room' ,MonHoc.TenMH AS 'subject' , Lop.TenLop AS 'classroom' ,  CaThucHanh.TenCa AS 'practiceCase',\n" +
                    "    (CONVERT(VARCHAR(8), CaThucHanh.ThoiGianBatDau, 108) + ' - ' + CONVERT(VARCHAR(8), CaThucHanh.ThoiGianKetThuc, 108)) AS 'time' , LichThucHanh.GhiChu AS 'note', LichThucHanh.LichID AS 'Id'\n" +
                    "FROM LichThucHanh\n" +
                    "INNER JOIN CaThucHanh ON CaThucHanh.CaID = LichThucHanh.CaID_FK\n" +
                    "INNER JOIN NguoiDung ON NguoiDung.UserID = LichThucHanh.UserID_FK\n" +
                    "INNER JOIN Lop ON Lop.LopID = LichThucHanh.LopID_FK\n" +
                    "INNER JOIN MonHoc ON MonHoc.MonHocID = LichThucHanh.MonHocID_FK\n" +
                    "INNER JOIN LTH_Phong ON LTH_Phong.LichID_FK = LichThucHanh.LichID\n" +
                    "INNER JOIN PhongThucHanh ON LTH_Phong.PhongID_FK = PhongThucHanh.PhongID";
        } else if (userCurrent.getRole().getNameRole().equals("GV")) {
            sql = "\tSELECT\n" +
                    "        LichThucHanh.Ngay AS 'date' ,\n" +
                    "        ( NguoiDung.Ho + ' ' + NguoiDung.Ten) AS 'teacher' ,\n" +
                    "        PhongThucHanh.TenPhong AS 'room' ,\n" +
                    "        ISNULL(MonHoc.TenMH ,'') AS 'subject' ,\n" +
                    "        ISNULL(Lop.TenLop ,'') AS 'classroom' ,\n" +
                    "        CaThucHanh.TenCa AS 'practiceCase',\n" +
                    "        (CONVERT(VARCHAR(8), CaThucHanh.ThoiGianBatDau, 108) + ' - ' + CONVERT(VARCHAR(8), CaThucHanh.ThoiGianKetThuc, 108)) AS 'time' ,\n" +
                    "        LichThucHanh.GhiChu AS 'note',\n" +
                    "        LichThucHanh.LichID AS 'Id' \n" +
                    "    FROM\n" +
                    "        LichThucHanh \n" +
                    "    LEFT JOIN\n" +
                    "        CaThucHanh \n" +
                    "            ON CaThucHanh.CaID = LichThucHanh.CaID_FK \n" +
                    "    LEFT JOIN\n" +
                    "        NguoiDung \n" +
                    "            ON NguoiDung.UserID = LichThucHanh.UserID_FK \n" +
                    "    LEFT JOIN\n" +
                    "        Lop \n" +
                    "            ON Lop.LopID = LichThucHanh.LopID_FK \n" +
                    "    LEFT JOIN\n" +
                    "        MonHoc \n" +
                    "            ON MonHoc.MonHocID = LichThucHanh.MonHocID_FK \n" +
                    "    LEFT JOIN\n" +
                    "        LTH_Phong \n" +
                    "            ON LTH_Phong.LichID_FK = LichThucHanh.LichID \n" +
                    "    LEFT JOIN\n" +
                    "        PhongThucHanh \n" +
                    "            ON LTH_Phong.PhongID_FK = PhongThucHanh.PhongID\n" +
                    "\tWHERE NguoiDung.UserID = " + idUser;
        }


        Query query = entityManager.createNativeQuery(sql);
        List<CalendarManagementDto> datas = new ArrayList<>();


        List<Object[]> results = query.getResultList();
        if(userCurrent.getRole().getNameRole().equals("GVU") ||
                userCurrent.getRole().getNameRole().equals("GV")){
            for (Object[] result : results) {
                CalendarManagementDto data = new CalendarManagementDto();
                data.setDate(dateFormat.format((Date) result[0]));
                data.setTeacher((String) result[1]);
                data.setRoom((String) result[2]);
                data.setSubject((String) result[3]);
                data.setClassroom((String) result[4]);
                data.setPracticeCase((String) result[5]);
                data.setTime((String) result[6]);
                data.setNote((String) result[7]);
                data.setId((result[8].toString()));
                datas.add(data);
            }
            return datas;
        }else if (userCurrent.getRole().getNameRole().equals("CSVC")){
            for (Object[] result : results) {
                CalendarManagementDto data = new CalendarManagementDto();
                data.setDate(dateFormat.format((Date) result[0]));
                data.setTeacher((String) result[1]);
                data.setRoom((String) result[2]);
                data.setSubject("");
                data.setClassroom("");
                data.setPracticeCase((String) result[5]);
                data.setTime((String) result[6]);
                data.setNote("");
                data.setId((result[8].toString()));
                datas.add(data);
            }
            return datas;
        }
    return null;
    }
}
