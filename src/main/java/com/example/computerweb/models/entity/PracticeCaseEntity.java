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
@Table(name = "TietThucHanh")
public class PracticeCaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TietID")
    private Long id ;

    @Column(name = "MaTiet")
    private  String namePracticeCase;

    @Column(name = "ThoiGianBatDau")
    private LocalTime timeStart;

    @Column(name = "ThoiGianKetThuc")
    private LocalTime timeEnd;

    @OneToMany(mappedBy = "practiceCase")
    private List<CalendarEntity> calendarEntities;

    @OneToMany(mappedBy = "practiceCase")
    private List<TicketRequestEntity> ticketRequestEntities;


}
