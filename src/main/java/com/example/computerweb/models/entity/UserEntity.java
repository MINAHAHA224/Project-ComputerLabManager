package com.example.computerweb.models.entity;

import com.example.computerweb.models.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;

@Getter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
@Table(name = "NguoiDung")
public class UserEntity extends AbstractEntity implements   Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Long id ;

    @Column(name = "MaNguoiDung")
    private String codeUser;

    @Column(name = "Ho")
    private String firstName;

    @Column(name = "Ten")
    private String lastName;

    @Column(name = "GioiTinh")
    private String gender;

    @Column(name = "NgaySinh")
    @Temporal(TemporalType.DATE)//ngày (yyyy-MM-dd) (không lưu thời gian)
    private Date dateOfBirth ;

    @Column(name = "SoDienThoai")
    private String phone ;



    @Column(name = "CCCD")
    private String infomationCode;



    @Column(name = "Diachi")
    private String address;


    @Column(name = "TinhThanh")
    private String province;

    @Column(name = "QuanHuyen")
    private String district;

    @Column(name = "PhuongXa")
    private String ward;

    @Column(name = "AnhDD")
    private String avatar;

    @ManyToOne
    @JoinColumn(name = "QuyenID_FK")
    private RoleEntity role;

    @ManyToOne
    @JoinColumn(name = "ChuyenNganh_FK")
    private MajorEntity major;



    @OneToMany(mappedBy = "user")
    private List<NotificationEntity> notificationEntities;



    @OneToOne(mappedBy = "user" , cascade = CascadeType.ALL)
    private  AccountEntity accountEntity;

    @OneToOne(mappedBy = "user" , cascade = CascadeType.ALL)
    private  AdvisorEntity advisor;

    @OneToMany(mappedBy = "userGui" , cascade = CascadeType.ALL)
    private  List<NotificationEntity> notificationEntity;

    @OneToMany(mappedBy = "user")
    private List<CreditClassEntity> creditClassEntities;

    @OneToMany(mappedBy = "user")
    private List<CalendarEntity> calendarEntities;

    @OneToMany(mappedBy = "user")
    private List<TicketRequestEntity> ticketRequestEntities;

    @OneToMany(mappedBy = "userCSVC")
    private List<TicketRequestEntity> ticketRequestCSVCs;

    @OneToMany(mappedBy = "userGVU")
    private List<TicketRequestEntity> ticketRequestGVUs;

}
