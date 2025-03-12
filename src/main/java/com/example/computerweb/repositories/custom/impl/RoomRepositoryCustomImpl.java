package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.models.entity.CalendarEntity;
import com.example.computerweb.models.entity.RoomEntity;
import com.example.computerweb.repositories.IRoomRepository;
import com.example.computerweb.repositories.custom.RoomRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@RequiredArgsConstructor
public class RoomRepositoryCustomImpl implements RoomRepositoryCustom {

    @PersistenceContext
    private EntityManager  entityManager;

    @Override
    @Transactional
    public void saveLTH_Phong(Long calendarId,List<Long> roomIds) {
        try {
            for ( Long roomId : roomIds){
                String sql = "INSERT LTH_Phong(LichID_FK , PhongID_FK) VALUES ("+calendarId+","+roomId+")";
                Query query = entityManager.createNativeQuery(sql);
                query.executeUpdate();
            }
        } catch (Exception e){
            System.out.println("---->ER : save loop roomId " + e.getMessage());
            e.printStackTrace();
        }



    }

    @Override
    @Transactional
    public void updateLTH_Phong(Long calendarId, List<Long> roomId) {
        try {
            for ( Long room : roomId ){
                String sql = "UPDATE LTH_Phong SET LTH_Phong.PhongID_FK ="+room+"  WHERE LTH_Phong.LichID_FK =" +calendarId;
                Query query = entityManager.createNativeQuery(sql);
                query.executeUpdate();
            }

        }catch (Exception e){
            System.out.println("---->ER : update loop roomId " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateLTH_PhongOnlyOne(Long calendarId, Long roomId) {
        String sql = "UPDATE LTH_Phong SET LTH_Phong.PhongID_FK ="+roomId+"  WHERE LTH_Phong.LichID_FK =" +calendarId;
        Query query = entityManager.createNativeQuery(sql);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void deleteLTH_Phong(Long calendarId) {
        try {
            String sql = "DELETE FROM LTH_Phong WHERE LTH_Phong.LichID_FK = " + calendarId;
            Query query = entityManager.createNativeQuery(sql);
            query.executeUpdate();
        }catch (Exception e){
            System.out.println("--ER : error delete LTH_Phong" + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public List<Long> getIdRoomsByCalendarIdOnLTH_Phong(Long calendarId) {
        String sql = "SELECT LTH_Phong.PhongID_FK FROM LTH_Phong WHERE LTH_Phong.LichID_FK =" +calendarId;

        Query query = entityManager.createNativeQuery(sql);
        List<Object> results = query.getResultList();
        List<Long> roomId = new ArrayList<>();
        for (Object result : results ){
            roomId.add(((Number)result).longValue());
        }
        return  roomId;
    }

    @Override
    public ResponseEntity<String> checkExistCalendarAndRoom(List<Long> listIdCalendar, List<Long> listIdRoom) {
        for ( Long idCalendar :listIdCalendar ){
            for ( Long idRoom :listIdRoom  ){
                String sql = "SELECT LTH_Phong FROM LTH_Phong WHERE LTH_Phong.LichID_FK =" +idCalendar + " AND LTH_Phong.PhongID_FK =" + idRoom;
                Query query = entityManager.createNativeQuery(sql);
                if(query.getResultList() != null){
                String nameRoom = findRoomById(idRoom);
                    return ResponseEntity.badRequest().body("Room : " + nameRoom + " is duplicated");
                }
            }
        }
        return ResponseEntity.ok().body("No room duplicated");
    }

    @Override
    public String findRoomById(Long idRoom) {

        String sql = " SELECT PhongThucHanh.TenPhong where PhongThucHanh.PhongID = " +idRoom;
        Query query = entityManager.createNativeQuery(sql);

        return query.getSingleResult().toString();
    }


}
