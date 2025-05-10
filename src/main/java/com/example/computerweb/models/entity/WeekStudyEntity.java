package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "TuanHoc")
public class WeekStudyEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TuanHocId")
    private Long id ;

    @Column(name = "MaTuan")
    private String codeWeek;

    @Column(name = "TenTuan")
    private String nameWeek;


}
