package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.CreditClassEntity;
import com.example.computerweb.models.entity.CreditClassToEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICreditClassToRepository extends JpaRepository<CreditClassToEntity , Long> {

    void deleteByCreditClass (CreditClassEntity creditClass);


}
