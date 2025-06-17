package com.example.computerweb.models.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(name = "Khoa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhoaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KhoaID")
    private Long id;

    @Column(name = "TenKhoa", nullable = false, unique = true)
    private String tenKhoa;

    @OneToMany(mappedBy = "khoa")
    private List<MajorEntity> majorEntities;
}
