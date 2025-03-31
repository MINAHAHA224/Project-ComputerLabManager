package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.SemesterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ISemesterRepository extends JpaRepository<SemesterEntity , Long> {

    SemesterEntity findSemesterEntityById ( Long id);
}
