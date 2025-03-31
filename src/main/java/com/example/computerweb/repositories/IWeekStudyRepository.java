package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.WeekStudyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface IWeekStudyRepository extends JpaRepository<WeekStudyEntity , Long> {
}
