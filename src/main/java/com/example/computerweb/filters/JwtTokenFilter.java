package com.example.computerweb.filters;

import com.example.computerweb.components.JwtTokenUtil;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.services.UserDetailsServiceCustom;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter
        extends OncePerRequestFilter {

    // Error code : 401  of  OncePerRequestFilter

    private final  JwtTokenUtil jwtTokenUtil;

    private final  UserDetailsService userDetailsService;



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
            //log.info("Token: {}", token);
            // check subject của claims trong token
            final String emailOfToken = this.jwtTokenUtil.extractEmailToken(token);
            // ***) boi vi sao thang JWT no chi chua email hay quyen thoi mà khong chua password mac du chua van duoc password
            // va tai sao chi check moi email thoi trong trương hop project cua minh
            // PHAI BIET LA : check JWT khi ma da dang nhap thanh cong => co token JWT roi moi check
            // ==> thu nhat : khong can chua password và khong can check password boi vi password da duoc chwck tu  login roi vi vay khi ma vo cac
            // api khong can check password măc du check van duoc nhung ma ton thoi gian , hay tuong tuong cu moi API lai phai giai ma password e check 1 lan
            // thi no rat ton thoi gian
            // ==> thu hai : tai sao JWT cua minh khong co thong tin cua quyen , boi vi minh khong xu dung JWT de check quyen ma minh sư dung UsernamePasswordAuthenticationToken
            // ket hop voi securityFilterChain de check quyen , Project cua minh la SAU MOI LAN check JWT , ma minh se add thong tin email và quyen vô UsernamePasswordAuthenticationToken
            // de securityFilterChain chech tiep quyen ==> giai dap van de la tai sao JWT cua minh ko co field quyen va khong check quyen o JWT
            // ==> thu ba : JWT  cua minh chi check email , thoi han token , chu ki dien tu thoi con quyen la securityFilterChain check

             if(emailOfToken != null && SecurityContextHolder.getContext().getAuthentication() == null ){
                 UserDetails userDetail =  this.userDetailsService.loadUserByUsername(emailOfToken);
                 if(jwtTokenUtil.validateToken(token ,userDetail )){

                     UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                             userDetail.getUsername(), null , userDetail.getAuthorities()
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
        String requestURI = request.getServletPath();
        if (requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/webjars")) {
            return true;
        }

        final List<Pair<String, String>> bybassTokens = Arrays.asList(
                Pair.of("/access/login" , "POST"),
                Pair.of("/access/forgotPassword", "POST")
                );

        for ( Pair<String , String > bybassToken : bybassTokens){

            String actionApi = request.getServletPath();
            String methodApi = request.getMethod();
            boolean testAction = bybassToken.getFirst().equals(actionApi);
            boolean testMethod= bybassToken.getSecond().equals(methodApi);
            if ( bybassToken.getFirst().equals(actionApi) && bybassToken.getSecond().equals(methodApi)){
                return true;
            }
        }

        return false;
    }
}
