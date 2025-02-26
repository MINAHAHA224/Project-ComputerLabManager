package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.DTO.dto.CalendarManagementDto;
import com.example.computerweb.repositories.custom.CalendarRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class CalendarRepositoryCustomImpl implements CalendarRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<CalendarManagementDto> findAllCustom() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String sql = "SELECT  LichThucHanh.Ngay AS 'date' ,( NguoiDung.Ho + ' ' + NguoiDung.Ten) AS 'teacher' , PhongThucHanh.TenPhong AS 'room' ,MonHoc.TenMH AS 'subject' , Lop.TenLop AS 'classroom' ,  CaThucHanh.TenCa AS 'practiceCase',\n" +
                "    (CONVERT(VARCHAR(8), CaThucHanh.ThoiGianBatDau, 108) + ' - ' + CONVERT(VARCHAR(8), CaThucHanh.ThoiGianKetThuc, 108)) AS 'time' , LichThucHanh.GhiChu AS 'note'\n" +
                "FROM LichThucHanh\n" +
                "INNER JOIN CaThucHanh ON CaThucHanh.CaID = LichThucHanh.CaID_FK\n" +
                "INNER JOIN NguoiDung ON NguoiDung.UserID = LichThucHanh.UserID_FK\n" +
                "INNER JOIN Lop ON Lop.LopID = LichThucHanh.LopID_FK\n" +
                "INNER JOIN MonHoc ON MonHoc.MonHocID = LichThucHanh.MonHocID_FK\n" +
                "INNER JOIN LTH_Phong ON LTH_Phong.LichID_FK = LichThucHanh.LichID\n" +
                "INNER JOIN PhongThucHanh ON LTH_Phong.PhongID_FK = PhongThucHanh.PhongID";

        Query query = entityManager.createNativeQuery(sql);
        List<CalendarManagementDto> datas = new ArrayList<>();


        List<Object[]> results = query.getResultList();
        for ( Object[] result : results){
            CalendarManagementDto data = new CalendarManagementDto();
            data.setDate(dateFormat.format((Date)result[0]));
            data.setTeacher((String)result[1]);
            data.setRoom((String)result[2]);
            data.setSubject((String)result[3]);
            data.setClassroom((String)result[4]);
            data.setPracticeCase((String)result[5]);
            data.setTime((String)result[6]);
            data.setNote((String)result[7]);
            datas.add(data);
        }
        return datas;
    }
}
