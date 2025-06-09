package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.ClassroomEntity;
import com.example.computerweb.models.entity.CreditClassEntity;
import com.example.computerweb.models.entity.SubjectEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.custom.CreditClassRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ICreditClassRepository extends JpaRepository<CreditClassEntity, Long >  , CreditClassRepositoryCustom {
    CreditClassEntity findCreditClassEntityById(Long id);
    List<CreditClassEntity> findAllByUser(UserEntity user);
    boolean existsCreditClassEntitiesBySubjectAndAndClassroom (SubjectEntity subjectEntity , ClassroomEntity classroomEntity);
}
