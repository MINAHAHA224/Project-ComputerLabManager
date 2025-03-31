package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.models.entity.CalendarEntity;
import com.example.computerweb.models.entity.RoomEntity;
import com.example.computerweb.repositories.IRoomRepository;
import com.example.computerweb.repositories.custom.RoomRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@RequiredArgsConstructor
@Repository
public class RoomRepositoryCustomImpl implements RoomRepositoryCustom {




}
