package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.*;

import com.example.computerweb.repositories.custom.CalendarRepositoryCustom;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface ICalendarRepository extends JpaRepository<CalendarEntity , Long> , CalendarRepositoryCustom {
        CalendarEntity findCalendarEntityById (Long id);
        boolean   existsByCreditClassAndWeekSemesterAndDayAndPracticeCaseAndRoom (CreditClassEntity creditClass , WeekSemesterEntity weekSemester , Long day , PracticeCaseEntity practiceCase , RoomEntity room);

        // check exist rent room
        boolean existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus(WeekSemesterEntity weekSemester , Long day , PracticeCaseEntity practiceCase, RoomEntity room , StatusEntity status);

        boolean existsByCreditClass ( CreditClassEntity creditClass);
        void deleteById( Long id);

        int countCalendarEntityByStatus ( StatusEntity status);

        List<CalendarEntity> findAllByWeekSemesterAndDayAndRoomAndStatus(
                WeekSemesterEntity weekSemester,
                Long day,
                RoomEntity room,
                StatusEntity status
        );


        @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END " +
                "FROM CalendarEntity c " +
                "WHERE c.id <> :currentCalendarId " + // Loại trừ lịch đang cập nhật
                "AND c.room = :room " +
                "AND c.weekSemester = :weekSemester " +
                "AND c.day = :day " +
                "AND c.status = :status " +
                // Điều kiện chồng chéo khoảng tiết
                // Giả sử PracticeCaseEntity có ID là Long và liên tục
                "AND c.practiceCase.id <= (:newPracticeCaseEndId) " +        // Tiết bắt đầu của lịch khác <= Tiết kết thúc của lịch mới
                "AND (c.practiceCase.id + c.allCase - 1) >= :newPracticeCaseBeginId") // Tiết kết thúc của lịch khác >= Tiết bắt đầu của lịch mới
        boolean existsOverlappingCalendar(
                @Param("currentCalendarId") Long currentCalendarId,
                @Param("room") RoomEntity room,
                @Param("weekSemester") WeekSemesterEntity weekSemester,
                @Param("day") Long day,
                @Param("status") StatusEntity status,
                @Param("newPracticeCaseBeginId") Long newPracticeCaseBeginId, // ID của practiceCaseNew
                @Param("newPracticeCaseEndId") Long newPracticeCaseEndId     // ID của practiceCaseNew + allCase - 1
        );
}
