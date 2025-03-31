package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "ChuyenNganh")
public class MajorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ChuyenNganhID")
    private Long id ;

    @Column(name = "MaChuyenNganh")
    private String codeMajor;

    @Column(name = "NoiDungCN")
    private String contentMajor;

    @OneToMany(mappedBy = "major")
    private List<UserEntity> userEntities;
}
