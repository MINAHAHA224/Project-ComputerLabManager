package com.example.computerweb.services;

import com.example.computerweb.models.entity.AccountEntity;
import com.example.computerweb.repositories.IAccountRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceCustom implements UserDetailsService {


    private final IAccountRepository iAccountRepository;
    public  UserDetailsServiceCustom ( IAccountRepository iAccountRepository){
        this.iAccountRepository = iAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

      Optional<AccountEntity>  account = this.iAccountRepository.findAccountEntityByEmail(username) ;
      if (account.isPresent() ){
          String email = account.get().getEmail();
          String password = account.get().getPassword();
              List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
              authorityList.add( new SimpleGrantedAuthority("ROLE_" + account.get().getUser().getRole().getNameRole().toUpperCase()));
              // return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + getRole().getName().toUpperCase()));
          return new User(
                  email,
                  password ,
                  authorityList
          );
      }else {
          throw new UsernameNotFoundException("Email does not exist");
      }
    }
}
