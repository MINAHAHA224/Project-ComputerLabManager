package com.example.computerweb.models.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "TaiKhoan")
public class AccountEntity implements Serializable , UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TaiKhoanId")
    private Long id ;

    @Column(name = "Email")
    private String email;

    @Column(name = "EmailCN")
    private  String emailOfPersonal ;

    @Column(name = "MatKhau")
    private String passWord;





    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "UserID" )
    private UserEntity user;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add( new SimpleGrantedAuthority("ROLE_" + user.getRole().getNameRole().toUpperCase()));
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
}
