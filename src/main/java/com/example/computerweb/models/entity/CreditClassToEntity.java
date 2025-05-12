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
@Table(name = "LopTinChi_ToHop")
public class CreditClassToEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ToHopID")
    private Long id;

    @Column(name = "MaTo")
    private String Mato;

    @ManyToOne
    @JoinColumn(name = "LopTinChi_FK")
    private CreditClassEntity creditClass;


}
