package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.PracticeCaseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface IPracticeCaseRepository  extends JpaRepository<PracticeCaseEntity , Long> {
    PracticeCaseEntity findPracticeCaseEntityById ( Long id);

    @Query("SELECT pc FROM PracticeCaseEntity pc WHERE pc.id >= :startId ORDER BY pc.id ASC")
    List<PracticeCaseEntity> findPracticeCasesStartingFromId(@Param("startId") Long startId, Pageable pageable);




}
