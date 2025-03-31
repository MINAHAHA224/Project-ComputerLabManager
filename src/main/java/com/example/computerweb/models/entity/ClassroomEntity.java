package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Lop")
public class ClassroomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LopID")
    private Long id ;

    @Column(name = "MaLop")
    private String nameClassroom;

    @Column(name = "SoLuongSV")
    private Long numberOfStudents;

    @Column(name = "Khoa")
    private  String nameOfFaculty;

    @ManyToOne
    @JoinColumn(name = "CVHT_FK")
    private  AdvisorEntity advisor;

    @OneToMany(mappedBy = "classroom")
    private List<CreditClassEntity> creditClassEntities;





}
