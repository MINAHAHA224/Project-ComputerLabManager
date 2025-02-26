package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
@Table(name = "LichThucHanh")
public class CalendarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LichID")
    private Long id ;

    @Column(name = "GhiChu")
    private String noteCalendar ;

    @Column(name = "Ngay")
    @Temporal(TemporalType.DATE)
    private Date dateOfCalendar ;



    @ManyToOne
    @JoinColumn(name = "UserID_FK")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "CaID_FK")
    private PracticeCaseEntity practiceCase ;

    @ManyToOne
    @JoinColumn(name = "LopID_FK")
    private ClassroomEntity classroom ;

    @ManyToOne
    @JoinColumn(name = "MonHocID_FK")
    private SubjectEntity subject;
//
//    @Column(name = "MonHocID_FK")
//    private Long subjectId ;

}
