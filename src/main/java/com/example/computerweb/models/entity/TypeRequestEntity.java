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
@Table(name = "LoaiYC")
public class TypeRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LoaiYcID")
    private Long id ;

    @Column(name = "TenLoaiYC")
    private String nameTypeRequest;

    @Column(name = "NDloaiYC")
    private String contentTypeRequest;

    @OneToMany(mappedBy = "typeRequest")
    private List<TicketRequestEntity> ticketRequestEntities;

}
