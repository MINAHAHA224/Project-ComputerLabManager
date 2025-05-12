package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.FacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFacilityRepository extends JpaRepository<FacilityEntity, Long> {
    FacilityEntity findFacilityEntityById(Long id);
}
