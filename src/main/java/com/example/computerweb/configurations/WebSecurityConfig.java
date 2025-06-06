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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

//@Configuration
//@EnableWebSecurity
//@EnableWebMvc
//@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    // lỗi 403 : forbiden do Spring Security

//    private final   JwtTokenFilter jwtTokenFilter;
//
//    private final  AuthenticationProvider authenticationProvider;
//
//
//
//    @Bean
//    public SessionRegistry sessionRegistry() {
//        return new SessionRegistryImpl();
//    }
//
//    // Rất quan trọng: Bean này lắng nghe các sự kiện tạo và hủy session
//    // để SessionRegistry có thể được cập nhật chính xác.
//    @Bean
//    public HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
//        http
//            // Error Code : 403 Forbidden of SpringSecurity
//
//                .csrf(AbstractHttpConfigurer::disable) // API dạng RESTful không dùng CSRF.
//
//                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
//                .addFilterBefore(this.jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
//                // minh addFilterBefore bởi vì mình muốn check JWT truoc , va trong cong doan check JWT a
//                // minh da add thong tin vo UsernamePasswordAuthenticationFilter  de phuc vu cho minh dich doan code tiep theo ne .hasRole()
//                // do la check quyen
//                .authorizeHttpRequests(request -> {
//                    request.requestMatchers(
//                            "/access/login" , "/access/forgotPassword"  ).permitAll();
//                    request.requestMatchers(
//                            "/calendarManagement/**" , "/userManagement/**" , "/creditClassManagement/**" ,"/creditClassManagement" ).hasRole("GVU");
//                    request.requestMatchers("/roomManagement" ,"/roomManagement/**" ).hasRole("CSVC");
//                    request.requestMatchers("/requestChangeCalendar/**" ,"/requestRentRoom" , "/notification/**"
//                            , "/requestTickets" ,"/requestTickets/**" , "/requestRentRoomDelete").hasRole("GV");
//                    request.requestMatchers("/requestManagement").hasAnyRole("GVU" , "CSVC");
//                    request.requestMatchers("/calendar" , "/home"  , "/profile" , "/access/logout").hasAnyRole("GVU" , "CSVC" , "GV");
//                    request.requestMatchers("/actuator/**", "/v3/**", "/webjars/**"
//                            , "/swagger-ui*/*swagger-initializer.js", "/swagger-ui*/**").permitAll();
//                    request.anyRequest().authenticated();
//                });
//            //.exceptionHandling(ex -> ex.accessDeniedPage());
//             //   .authenticationProvider(authenticationProvider);
//
//
//        return http.build();
//    }

    private final JwtTokenFilter jwtTokenFilter;




    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    // Rất quan trọng: Bean này lắng nghe các sự kiện tạo và hủy session
    // để SessionRegistry có thể được cập nhật chính xác.
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        http
                // Error Code : 403 Forbidden of SpringSecurity

                .csrf(AbstractHttpConfigurer::disable) // API dạng RESTful không dùng CSRF.

                // --- THAY ĐỔI QUAN TRỌNG VỀ SESSION MANAGEMENT ---
                .sessionManagement(session -> session
                        .sessionCreationPolicy(IF_REQUIRED) // HOẶC ALWAYS, nhưng IF_REQUIRED thường tốt hơn
                        .sessionAuthenticationStrategy(new RegisterSessionAuthenticationStrategy(sessionRegistry())) // Đăng ký session mới với registry
                        .maximumSessions(1) // Chỉ cho phép 1 session đồng thời cho mỗi user
                        .maxSessionsPreventsLogin(false) // false: login mới sẽ đá session cũ. true: login mới bị chặn.
                        // .expiredUrl("/login?session-expired") // (Tùy chọn) Chuyển hướng khi session bị đá
                        .sessionRegistry(sessionRegistry()) // Cung cấp SessionRegistry
                )
                // --- KẾT THÚC THAY ĐỔI SESSION MANAGEMENT ---
                .addFilterBefore(this.jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // minh addFilterBefore bởi vì mình muốn check JWT truoc , va trong cong doan check JWT a
                // minh da add thong tin vo UsernamePasswordAuthenticationFilter  de phuc vu cho minh dich doan code tiep theo ne .hasRole()
                // do la check quyen
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(
                            "/access/login" , "/access/forgotPassword"  ).permitAll();
                    request.requestMatchers(
                            "/calendarManagement/**" , "/userManagement/**" , "/creditClassManagement/**" ,"/creditClassManagement" , "/processRentRoom" ).hasRole("GVU");
                    request.requestMatchers("/processChangeCalendar").hasRole("TK");
                    request.requestMatchers("/roomManagement" ,"/roomManagement/**" , "/processChangeRoom" ).hasRole("CSVC");
                    request.requestMatchers("/requestChangeCalendar/**","/requestChangeRoom/**" ,"/requestRentRoom" , "/notification/**"
                            , "/requestTickets" ,"/requestTickets/**" ).hasRole("GV");
                    request.requestMatchers("/requestManagement").hasAnyRole("GVU" , "CSVC" , "TK");
                    request.requestMatchers("/calendar" , "/home"  , "/profile" , "/access/logout").hasAnyRole("GVU" , "CSVC" , "GV");
                    request.requestMatchers("/actuator/**", "/v3/**", "/webjars/**"
                            , "/swagger-ui*/*swagger-initializer.js", "/swagger-ui*/**").permitAll();
                    request.anyRequest().authenticated();
                });



        return http.build();
    }
}
