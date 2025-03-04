package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.CalendarEntity;
import com.example.computerweb.models.entity.PracticeCaseEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.custom.CalendarRepositoryCustom;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;


public interface ICalendarRepository extends JpaRepository<CalendarEntity , Long> , CalendarRepositoryCustom {
        CalendarEntity findCalendarEntityById (Long id);
        boolean existsByDateOfCalendarAndUserAndPracticeCase (Date dateOfCalendar , UserEntity user , PracticeCaseEntity practiceCase);
        void deleteById( Long id);
}
