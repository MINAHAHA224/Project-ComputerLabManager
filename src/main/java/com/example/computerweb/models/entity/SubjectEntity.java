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

    @Column(name = "MaMH")
    private String codeSubject;

    @Column(name = "TongTiet")
    private Long allT ;

    @Column(name = "SoTLT")
    private Long SoTLT;

    @Column(name = "SoTTH")
    private  Long SoTTH;

    @Column(name = "TongTC")
    private Long allCredit;

    @OneToOne(mappedBy = "subject")
    private CreditClassEntity creditClassEntity;

    @ManyToOne
    @JoinColumn(name = "HocKy_FK")
    private  SemesterEntity semester;



}
