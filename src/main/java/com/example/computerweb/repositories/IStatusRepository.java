package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IStatusRepository extends JpaRepository<StatusEntity , Long> {

    StatusEntity findStatusEntityById ( Long id);

   StatusEntity  findStatusEntityByNameStatus (String nameStt);
}
