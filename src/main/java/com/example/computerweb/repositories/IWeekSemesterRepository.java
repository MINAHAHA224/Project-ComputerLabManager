package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.SemesterEntity;
import com.example.computerweb.models.entity.WeekSemesterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface IWeekSemesterRepository extends JpaRepository<WeekSemesterEntity , Long> {

        WeekSemesterEntity findWeekSemesterEntityById ( Long id);
        List<WeekSemesterEntity> findAllBySemester (SemesterEntity semester);
}
