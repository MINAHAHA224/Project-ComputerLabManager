package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "HocKy")
public class SemesterEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HocKyId")
    private Long id;

    @Column(name = "KiHoc")
    private Long  semesterStudy ;

    @Column(name = "NamHoc")
    private String yearStudy;

    @OneToMany(mappedBy = "semester")
    private List<WeekSemesterEntity> weekSemesterEntities;

    @OneToMany(mappedBy = "semester")
    private  List<SubjectEntity> subjectEntities;
}
