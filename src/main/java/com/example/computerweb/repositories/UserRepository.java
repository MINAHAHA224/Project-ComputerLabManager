package com.example.computerweb.repositories;

import com.example.computerweb.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {

        Optional<User> findUserByEmail (String email);
}
