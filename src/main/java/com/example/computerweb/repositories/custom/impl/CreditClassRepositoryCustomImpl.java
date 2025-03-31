package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.DTO.dto.CreditClassEligibleDto;
import com.example.computerweb.repositories.custom.CreditClassRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CreditClassRepositoryCustomImpl implements CreditClassRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<CreditClassEligibleDto> findAllCreditClassEligible() {

        String sql = "WITH CTE AS (\n" +
                "    SELECT LopTinChiID_FK, SUM(SoTiet) AS TongSoTiet\n" +
                "    FROM (\n" +
                "        -- Lọc ra mỗi LopTinChiID_FK chỉ tính SoTiet 1 lần cho nhóm \"01\" hoặc \"02\"\n" +
                "        SELECT DISTINCT LopTinChiID_FK, LichThucHanh.Nhom, SoTiet\n" +
                "        FROM LichThucHanh\n" +
                "        WHERE LopTinChiID_FK IS NOT NULL \n" +
                "        AND LichThucHanh.Nhom IN ('01', '02')\n" +
                "    ) AS LTH\n" +
                "    GROUP BY LopTinChiID_FK\n" +
                ")\n" +
                "SELECT \n" +
                "    LopTinChi.LopTinChiID, \n" +
                "    MonHoc.MaMH, \n" +
                "    MonHoc.TenMH, \n" +
                "    Lop.MaLop,  \n" +
                "    Lop.SoLuongSV," +
                "    MonHoc.SoTTH AS SoTTHC, \n" +
                "    ISNULL(CTE.TongSoTiet, 0) AS TongSoTiet\n" +
                "FROM LopTinChi\n" +
                "INNER JOIN Lop ON Lop.LopID = LopTinChi.Lop_FK\n" +
                "INNER JOIN NguoiDung ON NguoiDung.UserID = LopTinChi.UserID_FK\n" +
                "INNER JOIN MonHoc ON MonHoc.MonHocID = LopTinChi.MonHoc_FK\n" +
                "LEFT JOIN CTE ON CTE.LopTinChiID_FK = LopTinChi.LopTinChiID\n" +
                "WHERE MonHoc.SoTTH > 0";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        List<CreditClassEligibleDto> data = new ArrayList<>();

        for ( Object[] result : results ){
            CreditClassEligibleDto creditClassEligibleDto = new CreditClassEligibleDto();
            creditClassEligibleDto.setCreditClassId( result[0].toString());
            creditClassEligibleDto.setCodeSubject( result[1].toString());
            creditClassEligibleDto.setNameSubject(result[2].toString());
            creditClassEligibleDto.setCodeClassroom( result[3].toString());
            creditClassEligibleDto.setStudentClassroom( result[4].toString());
            creditClassEligibleDto.setLessonSum(result[5].toString());
            creditClassEligibleDto.setLessonHave( result[6].toString());
            data.add(creditClassEligibleDto);
        }

        return data;
    }
}
