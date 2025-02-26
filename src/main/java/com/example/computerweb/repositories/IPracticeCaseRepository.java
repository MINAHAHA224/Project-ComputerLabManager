package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.PracticeCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPracticeCaseRepository  extends JpaRepository<PracticeCaseEntity , Long> {
    PracticeCaseEntity findPracticeCaseEntityById ( Long id);
}
