package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.ClassroomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface IClassroomRepository extends JpaRepository<ClassroomEntity , Long> {
    ClassroomEntity findClassroomEntityById ( Long id);
}
