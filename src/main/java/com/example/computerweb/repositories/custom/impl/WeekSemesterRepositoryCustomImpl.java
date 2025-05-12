package com.example.computerweb.repositories.custom.impl;


import com.example.computerweb.DTO.dto.semesterResponse.SemesterYearDto;
import com.example.computerweb.DTO.dto.semesterResponse.WeekTimeDto;
import com.example.computerweb.repositories.custom.WeekSemesterRepositoryCustom;
import com.example.computerweb.utils.DateUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class WeekSemesterRepositoryCustomImpl implements WeekSemesterRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SemesterYearDto> findAllSemesterYear() {
        String sql = " SELECT DISTINCT TuanHoc_KiHoc.HocKyId , TuanHoc_KiHoc.NamHoc FROM TuanHoc_KiHoc ";

        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> listResult = query.getResultList();
        List<SemesterYearDto> listAnswer = new ArrayList<>();
        for ( Object[] result : listResult){
            SemesterYearDto answer = new SemesterYearDto();
            answer.setIdSemester(result[0].toString());
            answer.setIdYear(result[1].toString());
            listAnswer.add(answer);
        }
        return listAnswer;
    }

    @Override
    public List<WeekTimeDto> findAllWeekTimeOfSemesterYear(String semesterYear) {

        String[] parts = semesterYear.split("-" , 2);
        int semesterFind = Integer.parseInt(parts[0]);
        String yearFind = parts[1];
        String sql = "  SELECT TuanHoc_KiHoc.TuanHoc_KiHoc_Id , TuanHoc_KiHoc.TuanHocId , TuanHoc_KiHoc.NgayBatDau , TuanHoc_KiHoc.NgayKetThuc\n" +
                "  FROM TuanHoc_KiHoc\n" +
                "  WHERE TuanHoc_KiHoc.HocKyId = "+semesterFind+" AND TuanHoc_KiHoc.NamHoc = '"+ yearFind +"'";

        Query  query = entityManager.createNativeQuery(sql);

        List<Object[]> listResult = query.getResultList();
        List<WeekTimeDto> listAnswer = new ArrayList<>();
        for ( Object[] result : listResult){
            WeekTimeDto answer = new WeekTimeDto();
            answer.setIdWeekTime(result[0].toString());
            answer.setWeek(result[1].toString());
            answer.setTimeBegin( (Date) result[2]);
            answer.setTimeEnd( (Date) result[3]);
            listAnswer.add(answer);
        }
        return listAnswer;
    }
}
