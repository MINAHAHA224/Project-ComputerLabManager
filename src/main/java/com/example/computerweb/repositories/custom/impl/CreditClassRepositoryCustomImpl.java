package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.DTO.dto.creditClassResponse.CreditClassEligibleDto;
import com.example.computerweb.DTO.dto.creditClassResponse.CreditClassRpPageIndexDto;
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

        String sql = "WITH TongTietTheoTo AS (\n" +
                "    SELECT \n" +
                "        LTH.LopTinChiID_FK,\n" +
                "        SUM(ISNULL(LTH.SoTiet, 0)) AS TongSoTietChoTungTo \n" +
                "    FROM LichThucHanh AS LTH\n" +
                "    WHERE LTH.LopTinChiID_FK IS NOT NULL \n" +
                "      AND LTH.ToHop IS NOT NULL \n" +
                "    GROUP BY \n" +
                "        LTH.LopTinChiID_FK, \n" +
                "        LTH.ToHop\n" +
                "),\n" +
                "CTE_SoTTHDC_Simplified AS (\n" +
                "    SELECT \n" +
                "        LopTinChiID_FK,\n" +
                "        MAX(TongSoTietChoTungTo) AS SoTTHDC \n" +
                "    FROM TongTietTheoTo\n" +
                "    GROUP BY LopTinChiID_FK\n" +
                ")\n" +
                "SELECT \n" +
                "    LTC.LopTinChiID, \n" +
                "    LTC.tenLopTinChi, \n" +
                "    MH.TenMH, \n" +
                "    LTC.SoLuongSVLTC, \n" +
                "    ISNULL(MH.SoTTH, 0) AS SoTTHC,      \n" +
                "    ISNULL(CTE.SoTTHDC, 0) AS SoTTHDC \n" +
                "FROM LopTinChi AS LTC\n" +
                "INNER JOIN MonHoc AS MH ON MH.MonHocID = LTC.MonHoc_FK\n" +
                "LEFT JOIN CTE_SoTTHDC_Simplified AS CTE ON CTE.LopTinChiID_FK = LTC.LopTinChiID \n" +
                "WHERE ISNULL(MH.SoTTH, 0) > 0 ";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        List<CreditClassEligibleDto> data = new ArrayList<>();

        for ( Object[] result : results ){
            CreditClassEligibleDto creditClassEligibleDto = new CreditClassEligibleDto();
            creditClassEligibleDto.setCreditClassId( result[0].toString());
            creditClassEligibleDto.setCodeCreditClass( result[1].toString());
            creditClassEligibleDto.setNameSubject(result[2].toString());

            creditClassEligibleDto.setStudentClassroom( result[3].toString());
            creditClassEligibleDto.setLessonSum(result[4].toString());
            creditClassEligibleDto.setLessonHave( result[5].toString());
            data.add(creditClassEligibleDto);
        }

        return data;
    }

    @Override
    public List<CreditClassRpPageIndexDto> findAllCreditForIndexPage() {

        String sql = "SELECT\n" +
                "        LopTinChi.LopTinChiID,\n" +
                "        LopTinChi.tenLopTinChi ,\n" +
                "        LopTinChi.SoLuongSvLTC ,\n" +
                "        MonHoc.MaMH ,\n" +
                "        NguoiDung.Ho + ' ' + NguoiDung.Ten AS GiangVien ,\n" +
                "        LopTinChi.Nhom,\n" +
                "        STRING_AGG(LopTinChi_ToHop.MaTo, ',') AS 'To' ,\n" +
                "        LopTinChi.SoTC ,\n" +
                "\t\tLop.MaLop\n" +
                "    FROM\n" +
                "        LopTinChi \n" +
                "\tINNER JOIN Lop ON Lop.LopID = LopTinChi.Lop_FK\n" +
                "    INNER JOIN\n" +
                "        MonHoc \n" +
                "            ON LopTinChi.MonHoc_FK = MonHoc.MonHocID \n" +
                "    INNER JOIN\n" +
                "        NguoiDung \n" +
                "            ON LopTinChi.UserID_FK = NguoiDung.UserID \n" +
                "    LEFT JOIN\n" +
                "        LopTinChi_ToHop \n" +
                "            ON LopTinChi_ToHop.LopTinChi_FK = LopTinChi.LopTinChiID \n" +
                "    GROUP BY\n" +
                "        LopTinChi.LopTinChiID,\n" +
                "        LopTinChi.tenLopTinChi,\n" +
                "        LopTinChi.SoLuongSvLTC,\n" +
                "        MonHoc.MaMH,\n" +
                "        NguoiDung.Ho,\n" +
                "        NguoiDung.Ten,\n" +
                "        LopTinChi.Nhom,\n" +
                "        LopTinChi.SoTC ,\n" +
                "\t\tLop.MaLop" ;
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        List<CreditClassRpPageIndexDto> answers = new ArrayList<>();
        for ( Object[] result : results){
            CreditClassRpPageIndexDto answer = new CreditClassRpPageIndexDto();
            answer.setCreditClassId( Long.valueOf(result[0].toString()));
            answer.setCodeCreditClass(result[1].toString());
            answer.setNumberOfStudentLTC(result[2].toString());
            answer.setCodeSubject(result[3].toString());
            answer.setTeacher(result[4].toString());
            answer.setGroup(result[5].toString().trim());
            answer.setCombination(result[6] != null ? result[6].toString() : "") ;
            answer.setCredit(Long.valueOf(result[7].toString()) );
            answer.setClassroom(result[8].toString());
            answers.add(answer);
        }

        return answers;
    }
}
