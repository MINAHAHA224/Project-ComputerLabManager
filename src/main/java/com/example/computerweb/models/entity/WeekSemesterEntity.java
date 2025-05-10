package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "TuanHoc_KiHoc")
public class WeekSemesterEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TuanHoc_KiHoc_Id")

    private Long id;
    @Column(name = "TuanHocId")
    private Long weekStudy;

    @Column(name = "HocKyId")
    private Long semesterStudy;

    @Column(name = "NgayBatDau")
    private Date dateBegin;

    @Column(name = "NgayKetThuc")
    private Date dateEnd;

    @Column(name = "NamHoc")
    private String yearStudy;

    @OneToMany(mappedBy = "weekSemester")
    private List<CalendarEntity> calendarEntities;

    @OneToMany(mappedBy = "weekSemester")
    private List<TicketRequestEntity> ticketRequestEntities;
}
