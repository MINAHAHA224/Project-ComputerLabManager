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

    @ManyToOne
    @JoinColumn( name = "LoaiYcID_FK")
    private TypeRequestEntity typeRequest;

    @ManyToOne
    @JoinColumn(name = "LichID_FK")
    private CalendarEntity calendar;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "NgayGui")
    private Date dateRequest;

    @ManyToOne
    @JoinColumn(name = "UserIdNguoiGui_FK")
    private UserEntity user;


    @ManyToOne
    @JoinColumn(name = "TuanHoc_KiHoc_Id_FK")
    private WeekSemesterEntity weekSemester;

    @Column(name = "Thu")
    private Long day;

    @ManyToOne
    @JoinColumn(name = "SoTietBD_FK")
    private PracticeCaseEntity practiceCase;

    @Column(name = "SoTiet")
    private Long allCase;

    @ManyToOne
    @JoinColumn(name = "PhongID_FK")
    private RoomEntity room;


    @Column(name = "GhiChu")
    private String noteTicket;

    @ManyToOne
    @JoinColumn(name = "DuyetCSVC")
    private StatusEntity statusCSVC;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_CSVC")
    private Date dateCreateCSVC;

    @ManyToOne
    @JoinColumn(name = "MODIFIED_CSVC")
    private UserEntity userCSVC;

    @ManyToOne
    @JoinColumn(name = "DuyetGVU")
    private StatusEntity statusGVU;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_GVU")
    private Date dateCreateGVU;

    @ManyToOne
    @JoinColumn(name = "MODIFIED_GVU")
    private UserEntity userGVU;

    @ManyToOne
    @JoinColumn(name = "TrangThaiID_FK")
    private StatusEntity statusTicket;


    @OneToOne(mappedBy = "ticketRequest" , cascade = CascadeType.ALL)
    private  NotificationEntity notificationEntity;


















}
