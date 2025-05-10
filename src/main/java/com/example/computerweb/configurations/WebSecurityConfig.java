package com.example.computerweb.configurations;

import com.example.computerweb.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.ExceptionHandlingDsl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    // lỗi 403 : forbiden do Spring Security

    private final   JwtTokenFilter jwtTokenFilter;

    private final  AuthenticationProvider authenticationProvider;





    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        http
            // Error Code : 403 Forbidden of SpringSecurity

                .csrf(AbstractHttpConfigurer::disable) // API dạng RESTful không dùng CSRF.

                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .addFilterBefore(this.jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // minh addFilterBefore bởi vì mình muốn check JWT truoc , va trong cong doan check JWT a
                // minh da add thong tin vo UsernamePasswordAuthenticationFilter  de phuc vu cho minh dich doan code tiep theo ne .hasRole()
                // do la check quyen
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(
                            "/access/login" , "/access/forgotPassword"  ).permitAll();
                    request.requestMatchers(
                            "/calendarManagement/**" , "/userManagement/**" , "/creditClassManagement/**" ,"/creditClassManagement" ).hasRole("GVU");
                    request.requestMatchers("/roomManagement" ,"/roomManagement/**" ).hasRole("CSVC");
                    request.requestMatchers("/requestChangeCalendar/**" ,"/requestRentRoom" , "/notification/**"
                            , "/requestTickets" ,"/requestTickets/**" , "/requestRentRoomDelete").hasRole("GV");
                    request.requestMatchers("/requestManagement").hasAnyRole("GVU" , "CSVC");
                    request.requestMatchers("/calendar" , "/home"  , "/profile" , "/access/logout").hasAnyRole("GVU" , "CSVC" , "GV");
                    request.requestMatchers("/actuator/**", "/v3/**", "/webjars/**"
                            , "/swagger-ui*/*swagger-initializer.js", "/swagger-ui*/**").permitAll();
                    request.anyRequest().authenticated();
                });
            //.exceptionHandling(ex -> ex.accessDeniedPage());
             //   .authenticationProvider(authenticationProvider);


        return http.build();
    }
}
