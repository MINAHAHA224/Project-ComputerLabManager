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
public class UserEntity extends AbstractEntity implements UserDetails , Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Long id ;

    @Column(name = "Ho")
    private String firstName;

    @Column(name = "Ten")
    private String lastName;

    @Column(name = "GioiTinh")
    private Gender gender;

    @Column(name = "NgaySinh")
    @Temporal(TemporalType.DATE)//ngày (yyyy-MM-dd) (không lưu thời gian)
    private Date dateOfBirth ;

    @Column(name = "SoDienThoai")
    private String phone ;

    @Column(name = "Email")
    private String email;

    @Column(name = "MatKhau")
    private String passWord;

    @Column(name = "CCCD")
    private String infomationCode;

    @Column(name = "ChuyenNganh")
    private String major;

    @Column(name = "Diachi")
    private String address;

    @Column(name = "EmailCN")
    private String emailPersonal;

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

    @OneToMany(mappedBy = "user")
    private List<CalendarEntity> calendarEntities;

    @OneToMany(mappedBy = "user")
    private List<NotificationEntity> notificationEntities;

    @OneToMany(mappedBy = "user")
    private List<TicketRequestEntity> ticketRequestEntities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add( new SimpleGrantedAuthority("ROLE_" + getRole().getNameRole().toUpperCase()));
        return authorityList;

       // return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + getRole().getName().toUpperCase()));
    }


    @Override
    public String getPassword() {
        return this.passWord;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
