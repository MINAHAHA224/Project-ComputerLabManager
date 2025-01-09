package com.example.computerweb.filters;

import com.example.computerweb.components.JwtTokenUtil;
import com.example.computerweb.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    // Error code : 401  of  OncePerRequestFilter
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            if (isBybassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            // check xem có gửi về token không
            final String authHeader = request.getHeader("Authorization");
            if ( authHeader == null || !authHeader.startsWith("Bearer ")){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED ,"Unauthorized");
                return;
            }

            // lấy token
            final String token = authHeader.substring(7);
            // check subject của claims trong token
            final String emailOfToken = this.jwtTokenUtil.extractEmailToken(token);

             if(emailOfToken != null && SecurityContextHolder.getContext().getAuthentication() != null ){
                 User userDetail = (User) this.userDetailsService.loadUserByUsername(emailOfToken);
                 if(jwtTokenUtil.validateToken(token ,userDetail )){

                     UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                             userDetail.getEmail(), null , userDetail.getAuthorities()
                     );
                     authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                     // get all context() ra để xét authentication cho các trang code trong dự án để  lấy được thông tin người dùng
                     // mục đích là check xem còn hạn ko nếu còn thì quang thông tin nguoi dùng như tren
                     SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                 }
                 else {
                     throw new  BadCredentialsException("Token hết hạn vui lòng đng nhập lại");
                 }
                 filterChain.doFilter(request , response);
             }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }

    }

    private boolean isBybassToken(@NonNull HttpServletRequest request) {
        // ==> không thể sài HashMap<> cho trường hợp này được vì  1 api có nhiều Method( tức là 1 key , có nhiều value )
        // nếu mà trùng key ( tức là trùng api ) thì value sẽ bị replace
        // ==> tóm lại chỉ cần trùng key là nó bị triệt tiêu ==> giải pháp là dùng Pair

//        log.error("method: {}, endpoint{}", request.getMethod(), request.getServletPath());
//
//        HashMap<String, String> apiTokens = new HashMap<>();
//        apiTokens.put(String.format("%s/users/login", apiPrefix), "POST");
////        apiTokens.put(String.format("%s/users/login", apiPrefix), "GET");
//        apiTokens.put(String.format("%s/users/register", apiPrefix), "POST");
//
//
//        for (Map.Entry<String, String> apiToken : apiTokens.entrySet()) {
//            String testRequestSevletPath = request.getServletPath();
//            boolean testAction = apiToken.getKey().equals(request.getServletPath());
//            boolean testMethod = apiToken.getValue().equals(request.getMethod());
//            if (apiToken.getKey().equals(request.getServletPath()) && apiToken.getValue().equals(request.getMethod())) {
//                return true;
//            }
//        }

        final List<Pair<String, String>> bybassTokens = Arrays.asList(
                Pair.of(String.format("%s/users/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/register", apiPrefix), "POST")
                );

        for ( Pair<String , String > bybassToken : bybassTokens){

            String actionApi = request.getServletPath();
            String methodApi = request.getMethod();
            if ( bybassToken.getFirst().equals(actionApi) && bybassToken.getSecond().equals(methodApi)){
                return true;
            }
        }

        return false;
    }
}
