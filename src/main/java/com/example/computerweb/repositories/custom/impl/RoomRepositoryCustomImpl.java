package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.repositories.custom.RoomRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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


}
