package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "MonHoc")
public class SubjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MonHocID")
    private Long id ;

    @Column(name = "TenMH")
    private String nameSubject ;

    @Column(name = "SoTLT")
    private Long SoTLT;

    @Column(name = "SoTTH")
    private  Long SoTTH;

    @OneToMany(mappedBy = "subject")
    private List<CalendarEntity> calendarEntities;

    @OneToMany(mappedBy = "subject")
    private List<TicketRequestEntity> ticketRequestEntities;
}
