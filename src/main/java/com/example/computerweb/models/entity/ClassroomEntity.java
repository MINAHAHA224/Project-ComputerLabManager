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

    @Column(name = "TenLop")
    private String nameClassroom;

    @Column(name = "SoLuongSV")
    private Long numberOfStudents;

    @Column(name = "GVCN")
    private String nameTeacher ;

    @Column(name = "Khoa")
    private  String nameOfFaculty;

    @OneToMany(mappedBy = "classroom")
    private List<CalendarEntity> calendarEntities;

    @OneToMany(mappedBy = "classroomEntity")
    private List<TicketRequestEntity> ticketRequestEntities;

    @ManyToOne
    @JoinColumn(name = "CoSo_FK")
    private FacilityEntity facilityEntityClassroom;
}
