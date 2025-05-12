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
@Table(name = "CoSo")
public class FacilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CoSoID")
    private Long id ;

    @Column(name = "MaCS")
    private String nameFacility;

    @Column(name = "NDCoSo")
    private  String contentFacility;


    @OneToMany(mappedBy = "facility")
    private List<RoomEntity> roomEntities;



}
