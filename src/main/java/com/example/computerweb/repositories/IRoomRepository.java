package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.RoomEntity;
import com.example.computerweb.repositories.custom.RoomRepositoryCustom;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRoomRepository  extends JpaRepository<RoomEntity , Long> , RoomRepositoryCustom {

    RoomEntity findRoomEntityById ( Long id);
}
