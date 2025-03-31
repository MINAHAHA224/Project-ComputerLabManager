package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface IRoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findRoleEntityById (Long id);
}
