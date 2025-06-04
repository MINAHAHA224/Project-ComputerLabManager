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

        String sql = "WITH BuoiHocThucHanhDaXep AS (\n" +
                "    -- 1. Xác định các \"buổi học thực hành\" duy nhất đã được xếp cho mỗi lớp tín chỉ.\n" +
                "    SELECT DISTINCT\n" +
                "        LTH.LopTinChiID_FK,\n" +
                "        LTH.TuanHoc_KiHoc_Id_FK,\n" +
                "        LTH.Thu,\n" +
                "        LTH.SoTietBD_FK,\n" +
                "        LTH.SoTiet -- Đây là số tiết của buổi học đó\n" +
                "    FROM LichThucHanh AS LTH\n" +
                "    WHERE LTH.LopTinChiID_FK IS NOT NULL\n" +
                "      AND LTH.TrangThai_FK = 6 -- Chỉ tính các lịch có trạng thái active (hoặc trạng thái phù hợp khác)\n" +
                "),\n" +
                "TongSoTietThucHanhDaCo AS (\n" +
                "    -- 2. Tính tổng số tiết thực hành đã có cho mỗi lớp tín chỉ\n" +
                "    -- bằng cách cộng dồn SoTiet của các \"buổi học thực hành\" duy nhất đã xác định ở trên.\n" +
                "    SELECT\n" +
                "        BHTHDX.LopTinChiID_FK,\n" +
                "        SUM(ISNULL(BHTHDX.SoTiet, 0)) AS TongTietDaXepThucTe -- Đổi tên cột để rõ ràng hơn\n" +
                "    FROM BuoiHocThucHanhDaXep AS BHTHDX\n" +
                "    GROUP BY\n" +
                "        BHTHDX.LopTinChiID_FK\n" +
                ")\n" +
                "SELECT\n" +
                "    LTC.LopTinChiID,\n" +
                "    LTC.tenLopTinChi,\n" +
                "    MH.TenMH,\n" +
                "    LTC.SoLuongSVLTC,\n" +
                "    ISNULL(MH.SoTTH, 0) AS SoTTHC,  -- Số tiết thực hành chuẩn của môn học\n" +
                "    CASE\n" +
                "        -- Nếu tổng tiết đã xếp > số tiết chuẩn VÀ số tiết chuẩn > 0, thì lấy số tiết chuẩn\n" +
                "        WHEN ISNULL(TSTHDC.TongTietDaXepThucTe, 0) > ISNULL(MH.SoTTH, 0) AND ISNULL(MH.SoTTH, 0) > 0\n" +
                "            THEN ISNULL(MH.SoTTH, 0)\n" +
                "        -- Ngược lại, lấy tổng số tiết đã xếp (bao gồm cả trường hợp chưa xếp gì, hoặc đã xếp ít hơn/bằng số tiết chuẩn)\n" +
                "        ELSE ISNULL(TSTHDC.TongTietDaXepThucTe, 0)\n" +
                "    END AS SoTTHDC -- Số tiết thực hành đã được phân công (đã giới hạn bởi SoTTHC)\n" +
                "FROM LopTinChi AS LTC\n" +
                "INNER JOIN MonHoc AS MH ON MH.MonHocID = LTC.MonHoc_FK\n" +
                "LEFT JOIN TongSoTietThucHanhDaCo AS TSTHDC ON TSTHDC.LopTinChiID_FK = LTC.LopTinChiID\n" +
                "WHERE ISNULL(MH.SoTTH, 0) > 0 -- Chỉ hiển thị các lớp tín chỉ của môn có tiết thực hành\n" +
                "ORDER BY LTC.LopTinChiID ";

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
