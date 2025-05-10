package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.DTO.dto.subjectResponse.SubjectRpDto;
import com.example.computerweb.repositories.custom.SubjectRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class SubjectRepositoryCustomImpl implements SubjectRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;


    @Override
    public List<SubjectRpDto> findSubjectExistsSoTHH() {
        String sql = "SELECT MonHoc.MonHocID , MonHoc.TenMH  , MonHoc.MaMH , MonHoc.TongTC , MonHoc.KeHoachHocKy \n" +
                "\tFROM MonHoc\n" +
                "\tWHERE MonHoc.SoTTH > 0 " ;
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        List<SubjectRpDto> answers = new ArrayList<>();
        for ( Object[] result : results){
            SubjectRpDto subjectRpDto = new SubjectRpDto();
            subjectRpDto.setSubjectId(Long.valueOf(result[0].toString()));
            subjectRpDto.setContent(result[2].toString()  + " - " + result[1].toString() +" - " +"TC:" +result[3].toString() + " Kỳ học-Năm học: " + result[4].toString()  );

            answers.add(subjectRpDto);
        }

        return answers;
    }
}
