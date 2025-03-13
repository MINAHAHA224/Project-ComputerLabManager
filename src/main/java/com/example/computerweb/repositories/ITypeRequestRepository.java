package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.TypeRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITypeRequestRepository extends JpaRepository<TypeRequestEntity,Long > {

    TypeRequestEntity findTypeRequestEntityByNameTypeRequest ( String nameType);

    TypeRequestEntity findTypeRequestEntityById ( Long id);
}
