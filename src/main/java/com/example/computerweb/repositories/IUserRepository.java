package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.MajorEntity;
import com.example.computerweb.models.entity.RoleEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends JpaRepository<UserEntity, Long> , UserRepositoryCustom {



    UserEntity findUserEntityById (Long id);




    Optional<UserEntity> findUserEntityByPhone(String phone);

   Optional<List<UserEntity> > findAllByRoleAndMajor (RoleEntity  roleEntity , MajorEntity majorEntity);
    List<UserEntity> findUserEntitiesByRole(RoleEntity roleEntity);


    boolean existsByPhone(String phone);

    @Query("SELECT u FROM UserEntity u JOIN u.accountEntity a WHERE a.email = :email")
    Optional<UserEntity> findUserEntityByAccountEmail(@Param("email") String email);
}
