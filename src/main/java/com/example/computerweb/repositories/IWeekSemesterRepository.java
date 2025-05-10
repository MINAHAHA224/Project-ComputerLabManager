package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.SemesterEntity;
import com.example.computerweb.models.entity.WeekSemesterEntity;
import com.example.computerweb.repositories.custom.WeekSemesterRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface IWeekSemesterRepository extends JpaRepository<WeekSemesterEntity , Long> , WeekSemesterRepositoryCustom {

        WeekSemesterEntity findWeekSemesterEntityById ( Long id);
        List<WeekSemesterEntity> findAllBySemesterStudy (Long semesterStudy);


}
