package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "PhongThucHanh")
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PhongID")
    private Long id ;

    @Column(name = "TenPhong")
    private  String nameRoom ;

    @Column(name = "SoLuongMay")
    private Long numberOfComputers;

    @Column(name = "SoLuongMayHong")
    private Long numberOfComputerError;


    @ManyToOne
    @JoinColumn(name = "CoSo_FK")
    private FacilityEntity facility;

    @OneToMany(mappedBy = "room")
    private List<CalendarEntity> calendarEntities;

    @OneToMany(mappedBy = "room")
    private List<TicketRequestEntity> ticketRequestEntities;

}
