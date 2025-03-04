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
@Table(name = "ThongBao")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ThongBaoID")
    private Long id ;

    @Column(name = "TenTB")
    private String nameNotification;

    @Column(name = "NoiDungTB")
    private String contentNotification;

    @Column(name = "NgayThongBao")
    private Date dateNotification;

    @ManyToOne
    @JoinColumn(name = "UserID_FK")
    private  UserEntity user;

    @ManyToOne
    @JoinColumn(name = "TrangThaiID_FK")
    private StatusEntity status;
}
