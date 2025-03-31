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
@Table(name = "CVHocTap")
public class AdvisorEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cvhtId")
    private Long id;

    @Column(name = "NamHoc")
    private String year ;

    @OneToOne
    @JoinColumn(name = "UserID_FK")
    private UserEntity user;

    @OneToMany(mappedBy = "advisor")
    private List<ClassroomEntity> classroomEntities;
}
