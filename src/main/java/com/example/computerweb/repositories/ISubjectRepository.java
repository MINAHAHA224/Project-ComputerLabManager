package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ISubjectRepository extends JpaRepository<SubjectEntity , Long> {
    SubjectEntity findSubjectEntityById(Long id);
}
