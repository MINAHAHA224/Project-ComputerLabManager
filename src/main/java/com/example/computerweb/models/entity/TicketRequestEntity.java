package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "PhieuYeuCau")
public class TicketRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "YeuCauID")
    private Long id ;

    @Column(name = "GhiChu")
    private String noteTicket;

    @Column(name = "NgayCu")
    @Temporal(TemporalType.DATE)
    private Date dateOld ;

    @Column(name = "NgayMoi")
    @Temporal(TemporalType.DATE)
    private Date dateNew ;

    @Column(name = "CaCuID_FK")
    private String practiceCaseOld;

    @Column(name = "CaMoiID_FK")
    private String practiceCaseNew;

    @Column(name = "PhongIDCu_FK")
    private String roomOld;

    @Column(name = "PhongIDMoi_FK")
    private String roomNew;

    @Column(name = "LichId")
    private Long calendarId;
//    @ManyToOne
//    @JoinColumn(name = "CaCuID_FK")
//    private PracticeCaseEntity practiceCaseOld;
//
//    @ManyToOne
//    @JoinColumn(name = "CaMoiID_FK")
//    private PracticeCaseEntity practiceCaseNew;


    @ManyToOne
    @JoinColumn(name = "LoaiYcID_FK")
    private TypeRequestEntity typeRequest;

    @ManyToOne
    @JoinColumn(name = "UserID_FK")
    private UserEntity user;

//    @ManyToOne
//    @JoinColumn(name = "PhongID_FK")
//    private RoomEntity roomEntity;

    @ManyToOne
    @JoinColumn( name = "LopID_FK")
    private ClassroomEntity classroomEntity;


    @ManyToOne
    @JoinColumn(name = "MonHocID_FK")
    private SubjectEntity subject;

    @ManyToOne
    @JoinColumn(name = "TrangThaiID_FK")
    private StatusEntity status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "NgayGui")
    private Date dateRequest;

    @ManyToOne
    @JoinColumn(name = "DuyetCSVC")
    private StatusEntity doneCSVC;

    @ManyToOne
    @JoinColumn(name = "DuyetGVU")
    private StatusEntity doneGVU;


}
