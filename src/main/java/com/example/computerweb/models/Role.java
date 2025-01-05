package com.example.computerweb.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;

    @Column(name = "name")
    private String name;

    public static String GV = "GIAO_VIEN";
    public static String CBQL = "CAN_BO_QUAN_LY";
    public static String SV = "SINH_VIEN";
}
