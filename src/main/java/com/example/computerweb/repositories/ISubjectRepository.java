package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.SubjectEntity;
import com.example.computerweb.repositories.custom.SubjectRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ISubjectRepository extends JpaRepository<SubjectEntity , Long> , SubjectRepositoryCustom {
    SubjectEntity findSubjectEntityById(Long id);

 //   List<SubjectEntity> findSubjectEntitiesBySoTTHGreaterThan ( Long number);


}
