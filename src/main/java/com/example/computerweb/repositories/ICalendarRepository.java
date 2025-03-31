package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.*;

import com.example.computerweb.repositories.custom.CalendarRepositoryCustom;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface ICalendarRepository extends JpaRepository<CalendarEntity , Long> , CalendarRepositoryCustom {
        CalendarEntity findCalendarEntityById (Long id);
        boolean   existsByCreditClassAndWeekSemesterAndDayAndPracticeCaseAndRoom (CreditClassEntity creditClass , WeekSemesterEntity weekSemester , Long day , PracticeCaseEntity practiceCase , RoomEntity room);

        // check exist rent room
        boolean existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus(WeekSemesterEntity weekSemester , Long day , PracticeCaseEntity practiceCase, RoomEntity room , StatusEntity status);

        void deleteById( Long id);
}
