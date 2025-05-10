package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.AccountEntity;
import com.example.computerweb.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface IAccountRepository extends JpaRepository<AccountEntity , Long> {

   Optional<AccountEntity>  findAccountEntityByEmail (String email);



   AccountEntity findAccountEntityByEmailOfPersonal ( String email);
   boolean existsByEmail ( String email);

   AccountEntity findAccountEntityByUser (UserEntity user);

   boolean existsByEmailOfPersonal ( String email);

}
