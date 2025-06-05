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
@Table(name = "TrangThai")
public class StatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TrangThaiID")
    private Long id ;

    @Column(name = "MaTrangThai")
    private String nameStatus;

    @Column(name = "NDTrangThai")
    private String contentStatus;

    @OneToMany(mappedBy = "status")
    private List<NotificationEntity> notificationEntities;

    @OneToMany(mappedBy = "statusCSVC")
    private List<TicketRequestEntity> ticketRequestDoneCSVC;

    @OneToMany(mappedBy = "statusGVU")
    private List<TicketRequestEntity> ticketRequestDoneGVU;

    @OneToMany(mappedBy = "statusTK")
    private List<TicketRequestEntity> ticketRequestDoneTK;

    @OneToMany(mappedBy = "statusTicket")
    private List<TicketRequestEntity> ticketRequests;

    @OneToMany(mappedBy = "status")
    private  List<CalendarEntity> calendarEntities;

}
