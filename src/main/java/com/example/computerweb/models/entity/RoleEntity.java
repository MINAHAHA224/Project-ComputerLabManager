package com.example.computerweb.models.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "Quyen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QuyenID")
    private Long id ;

    @Column(name = "TenQuyen")
    private String nameRole;

    @Column(name = "NDQuyen")
    private String contentRole;

    @OneToMany(mappedBy = "role")
    private List<UserEntity> userEntities;

    public static String GV = "GIAO_VIEN";
    public static String CSVC = "CO_SO_VAT_CHAT";
    public static String GVU = "GIAO_VU";



}
