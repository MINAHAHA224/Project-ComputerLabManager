package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.TicketRequestEntity;
import com.example.computerweb.repositories.custom.TicketRequestCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITicketRequestRepository extends JpaRepository<TicketRequestEntity , Long> , TicketRequestCustom {
    TicketRequestEntity getTicketRequestEntityById(Long id);
}
