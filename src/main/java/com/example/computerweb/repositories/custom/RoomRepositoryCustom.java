package com.example.computerweb.repositories.custom;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepositoryCustom {

    void saveLTH_Phong (Long calendarId,List<Long> roomId);
    void deleteLTH_Phong (Long calendarId);
    List<Long> getIdRoomsByCalendarIdOnLTH_Phong (Long calendarId);
}
