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
@Table(name = "LopTinChi")
public class CreditClassEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LopTinChiID")
    private Long id;

    @Column(name = "SoTC")
    private Long credits;

    @ManyToOne
    @JoinColumn(name = "Lop_FK")
    private ClassroomEntity classroom;

    @OneToOne
    @JoinColumn(name = "MonHoc_FK")
    private SubjectEntity subject;

    @ManyToOne
    @JoinColumn(name = "UserID_FK")
    private UserEntity user;

    @OneToMany(mappedBy = "creditClass")
    private List<CalendarEntity> calendarEntities;
}
