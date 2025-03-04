package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "CaThucHanh")
public class PracticeCaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CaID")
    private Long id ;

    @Column(name = "TenCa")
    private  String namePracticeCase;

    @Column(name = "ThoiGianBatDau")
    private LocalTime timeStart;

    @Column(name = "ThoiGianKetThuc")
    private LocalTime timeEnd;

    @OneToMany(mappedBy = "practiceCase")
    private List<CalendarEntity> calendarEntities;

//    @OneToMany(mappedBy = "practiceCaseOld")
//    private List<TicketRequestEntity> ticketRequestEntitiesOld;
//
//    @OneToMany(mappedBy = "practiceCaseNew")
//    private List<TicketRequestEntity> ticketRequestEntitiesNew;


}
