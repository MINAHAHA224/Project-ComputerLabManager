package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
