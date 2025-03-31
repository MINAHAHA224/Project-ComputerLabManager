package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.MajorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface IMajorRepository extends JpaRepository<MajorEntity , Long> {

    MajorEntity findMajorEntityById(Long id);

    MajorEntity findMajorEntityByCodeMajor ( String codeMajor);
}
