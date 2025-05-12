package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "LopTinChiID_FK")
    private CreditClassEntity creditClass;

    @ManyToOne
    @JoinColumn(name = "UserIdMp_FK")
    private UserEntity user;





    @ManyToOne
    @JoinColumn(name = "TuanHoc_KiHoc_Id_FK")
    private WeekSemesterEntity weekSemester;

    @Column(name = "Thu")
    private Long day;

    @ManyToOne
    @JoinColumn(name = "SoTietBD_FK")
    private PracticeCaseEntity practiceCase;

    // SoTiet means How many practiceCase ?
    @Column(name = "SoTiet")
    private Long allCase ;

    @ManyToOne
    @JoinColumn(name = "PhongID_FK")
    private RoomEntity room;

    @ManyToOne
    @JoinColumn(name = "TrangThai_FK")
    private StatusEntity status;

    @Column(name = "GhiChu")
    private String noteCalendar ;

    @Column(name = "ToHop")
    private String group;

    @OneToMany(mappedBy = "calendar")
    private List<TicketRequestEntity> ticketRequestEntities;



}
