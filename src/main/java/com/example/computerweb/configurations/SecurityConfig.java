package com.example.computerweb.configurations;

import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.IAccountRepository;
import com.example.computerweb.repositories.IUserRepository;
import com.example.computerweb.services.UserDetailsServiceCustom;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final IUserRepository iUserRepository;
    private final IAccountRepository iAccountRepository;
    private  final UserDetailsServiceCustom userDetailsServiceCustom;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new UserDetailsServiceCustom(iAccountRepository);
//    }
    @Bean
    public UserDetailsService userDetailsService() {
       return email -> iAccountRepository.findAccountEntityByEmail(email)
               .orElseThrow(() ->
                       new UsernameNotFoundException(
                               "Cannot find user with email = " + email));
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
