package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.repositories.custom.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;



//    @Override
//    public UserEntity userByEmail(String email) {
//        String sql = "SELECT * FROM NguoiDung WHERE NguoiDung.SoDienThoai = '" +email+"'";
//        Query query = entityManager.createNativeQuery(sql.toString(), UserEntity.class);
//
//        try {
//            return (UserEntity) query.getSingleResult();
//        } catch (NoResultException e) {
//            return null;  // Tránh lỗi khi không tìm thấy kết quả
//        }
//
//    }
}
