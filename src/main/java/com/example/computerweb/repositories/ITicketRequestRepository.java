package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.StatusEntity;
import com.example.computerweb.models.entity.TicketRequestEntity;
import com.example.computerweb.models.entity.TypeRequestEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.custom.TicketRequestCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ITicketRequestRepository extends JpaRepository<TicketRequestEntity , Long> , TicketRequestCustom {
    TicketRequestEntity getTicketRequestEntityById(Long id);

    // for role GVU
    List<TicketRequestEntity> findAllByStatusCSVC (StatusEntity statusCSVC  );




    // for role CSVC
    List<TicketRequestEntity> findAllByUser (UserEntity user);
}
