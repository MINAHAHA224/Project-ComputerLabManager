package com.example.computerweb.repositories.custom;

import com.example.computerweb.models.entity.CalendarEntity;
import com.example.computerweb.models.entity.RoomEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository
public interface RoomRepositoryCustom {

    void saveLTH_Phong (Long calendarId,List<Long> roomId);

    void updateLTH_Phong ( Long calendarId,List<Long> roomId);

    void updateLTH_PhongOnlyOne ( Long calendarId,Long roomId);
    void deleteLTH_Phong (Long calendarId);
    List<Long> getIdRoomsByCalendarIdOnLTH_Phong (Long calendarId);

    ResponseEntity<String> checkExistCalendarAndRoom (List<Long> listCalendarId , List<Long> listRoomId);

    String findRoomById ( Long idRoom);
}
