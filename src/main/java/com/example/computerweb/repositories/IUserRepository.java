package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.RoleEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> , UserRepositoryCustom {

    UserEntity findUserEntityById (Long id);
    Optional<UserEntity> findUserEntityByEmail(String email);

    Optional<UserEntity> findUserEntityByPhone(String phone);

    List<UserEntity> findAllByRole(RoleEntity roleEntity);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
