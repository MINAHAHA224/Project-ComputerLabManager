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

    @Column(name = "TenTrangThai")
    private String nameStatus;

    @Column(name = "NDTrangThai")
    private String contentStatus;

    @OneToMany(mappedBy = "status")
    private List<NotificationEntity> notificationEntities;

    @OneToMany(mappedBy = "status")
    private List<TicketRequestEntity> ticketRequestEntities;

    @OneToMany(mappedBy = "doneCSVC")
    private List<TicketRequestEntity> ticketDoneCSVC;

    @OneToMany(mappedBy = "doneGVU")
    private List<TicketRequestEntity> ticketDoneGVU;
}
