package com.example.computerweb.filters;

import com.example.computerweb.components.JwtTokenUtil;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.services.UserDetailsServiceCustom;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

//    private final  JwtTokenUtil jwtTokenUtil;
//
//    private final  UserDetailsService userDetailsService;
//
//
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                    @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain filterChain)
//            throws ServletException, IOException {
//
//        try {
//            if (isBybassToken(request)) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            // check xem có gửi về token không
//            final String authHeader = request.getHeader("Authorization");
//            if ( authHeader == null || !authHeader.startsWith("Bearer ")){
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED ,"Không có quyền truy cập ");
//                return;
//            }
//
//            // lấy token
//            final String token = authHeader.substring(7);
//            //log.info("Token: {}", token);
//            // check subject của claims trong token
//            final String emailOfToken = this.jwtTokenUtil.extractEmailToken(token);
//            // ***) boi vi sao thang JWT no chi chua email hay quyen thoi mà khong chua password mac du chua van duoc password
//            // va tai sao chi check moi email thoi trong trương hop project cua minh
//            // PHAI BIET LA : check JWT khi ma da dang nhap thanh cong => co token JWT roi moi check
//            // ==> thu nhat : khong can chua password và khong can check password boi vi password da duoc chwck tu  login roi vi vay khi ma vo cac
//            // api khong can check password măc du check van duoc nhung ma ton thoi gian , hay tuong tuong cu moi API lai phai giai ma password e check 1 lan
//            // thi no rat ton thoi gian
//            // ==> thu hai : tai sao JWT cua minh khong co thong tin cua quyen , boi vi minh khong xu dung JWT de check quyen ma minh sư dung UsernamePasswordAuthenticationToken
//            // ket hop voi securityFilterChain de check quyen , Project cua minh la SAU MOI LAN check JWT , ma minh se add thong tin email và quyen vô UsernamePasswordAuthenticationToken
//            // de securityFilterChain chech tiep quyen ==> giai dap van de la tai sao JWT cua minh ko co field quyen va khong check quyen o JWT
//            // ==> thu ba : JWT  cua minh chi check email , thoi han token , chu ki dien tu thoi con quyen la securityFilterChain check
//
//             if(emailOfToken != null && SecurityContextHolder.getContext().getAuthentication() == null ){
//                 UserDetails userDetail =  this.userDetailsService.loadUserByUsername(emailOfToken);
//
//                 // lay token tu session
//                 HttpSession session = request.getSession(false); // Lấy session hiện tại, không tạo mới
//                 String jwtFromSession = null;
//
//                 if (session != null) { // Quan trọng: Kiểm tra session có tồn tại và hợp lệ không
//                     jwtFromSession = (String) session.getAttribute("USER_ACTIVE_JWT");
//                 } else {
//                     // Nếu không có session, hoặc session đã bị invalidate (ví dụ do bị "đá")
//                     // thì không thể xác thực dựa trên token trong session
//                     System.out.println("No active session found for this request. Potentially expired or invalidated.");
//                      SecurityContextHolder.clearContext();
//                      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session hết hạn vui lòng đng nhập lại");
////                      filterChain.doFilter(request, response); // Cho qua để các cơ chế khác xử lý hoặc trả lỗi 401/403
//
//                      return;
//
////                     throw new  BadCredentialsException("Session hết hạn vui lòng đng nhập lại");
//                 }
//                 if(jwtTokenUtil.validateToken(token ,userDetail  , jwtFromSession)){
//
//                     UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                             userDetail.getUsername(), null , userDetail.getAuthorities()
//                     );
//                     authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                     // get all context() ra để xét authentication cho các trang code trong dự án để  lấy được thông tin người dùng
//                     // mục đích là check xem còn hạn ko nếu còn thì quang thông tin nguoi dùng như tren
//                     SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                 }
//                 else {
//                     SecurityContextHolder.clearContext();
//                     response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Không có quyền truy cập");
////                     throw new  BadCredentialsException("Token hết hạn vui lòng đng nhập lại");
//                     return;
//                 }
//                 filterChain.doFilter(request , response);
//             }
//
//        } catch (Exception e) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Không có quyền truy cập");
//        }
//
//    }
//
//    private boolean isBybassToken(@NonNull HttpServletRequest request) {
//        // ==> không thể sài HashMap<> cho trường hợp này được vì  1 api có nhiều Method( tức là 1 key , có nhiều value )
//        // nếu mà trùng key ( tức là trùng api ) thì value sẽ bị replace
//        // ==> tóm lại chỉ cần trùng key là nó bị triệt tiêu ==> giải pháp là dùng Pair
//
////        log.error("method: {}, endpoint{}", request.getMethod(), request.getServletPath());
////
////        HashMap<String, String> apiTokens = new HashMap<>();
////        apiTokens.put(String.format("%s/users/login", apiPrefix), "POST");
//////        apiTokens.put(String.format("%s/users/login", apiPrefix), "GET");
////        apiTokens.put(String.format("%s/users/register", apiPrefix), "POST");
////
////
////        for (Map.Entry<String, String> apiToken : apiTokens.entrySet()) {
////            String testRequestSevletPath = request.getServletPath();
////            boolean testAction = apiToken.getKey().equals(request.getServletPath());
////            boolean testMethod = apiToken.getValue().equals(request.getMethod());
////            if (apiToken.getKey().equals(request.getServletPath()) && apiToken.getValue().equals(request.getMethod())) {
////                return true;
////            }
////        }
//        String requestURI = request.getServletPath();
//        if (requestURI.startsWith("/swagger-ui") ||
//                requestURI.startsWith("/v3/api-docs") ||
//                requestURI.startsWith("/swagger-resources") ||
//                requestURI.startsWith("/webjars")) {
//            return true;
//        }
//
//        final List<Pair<String, String>> bybassTokens = Arrays.asList(
//                Pair.of("/access/login" , "POST"),
//                Pair.of("/access/forgotPassword", "POST")
//                );
//
//        for ( Pair<String , String > bybassToken : bybassTokens){
//
//            String actionApi = request.getServletPath();
//            String methodApi = request.getMethod();
//            boolean testAction = bybassToken.getFirst().equals(actionApi);
//            boolean testMethod= bybassToken.getSecond().equals(methodApi);
//            if ( bybassToken.getFirst().equals(actionApi) && bybassToken.getSecond().equals(methodApi)){
//                return true;
//            }
//        }
//
//        return false;
//    }
private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService; // Spring sẽ inject bean UserDetailsService của bạn

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            if (isBypassToken(request)) {
                log.debug("Bypassing JWT filter for: {}", request.getServletPath());
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Authorization header is missing or does not start with Bearer for: {}", request.getServletPath());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Yêu cầu header Authorization với Bearer token.");
                return;
            }

            final String tokenFromHeader = authHeader.substring(7);
            final String emailFromToken;

            try {
                emailFromToken = this.jwtTokenUtil.extractEmailFromSubject(tokenFromHeader);
            } catch (ExpiredJwtException e) {
                log.warn("JWT token from header is expired: {} for: {}", e.getMessage(), request.getServletPath());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token đã hết hạn.");
                return;
            } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
                log.warn("Invalid JWT token from header: {} for: {}", e.getMessage(), request.getServletPath());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ.");
                return;
            }
//&& SecurityContextHolder.getContext().getAuthentication() == null
            if (emailFromToken != null ) {
                UserDetails userDetails;
                try {
                    userDetails = this.userDetailsService.loadUserByUsername(emailFromToken);
                } catch (UsernameNotFoundException e) {
                    log.warn("User not found for email from token: {} for: {}", emailFromToken, request.getServletPath());
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Người dùng không tồn tại cho token này.");
                    return;
                }

                HttpSession session = request.getSession(false);
                if (session == null) {
                    log.warn("No active HTTP session found (session might have been invalidated/kicked) for user: {} path: {}", emailFromToken, request.getServletPath());
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Phiên làm việc không hợp lệ hoặc đã bị đăng xuất từ nơi khác.");
                    return;
                }

                String jwtFromSession = (String) session.getAttribute("USER_ACTIVE_JWT");
                if (jwtFromSession == null) {
                    log.warn("No USER_ACTIVE_JWT found in current session for user: {} path: {}", emailFromToken, request.getServletPath());
                    // Điều này có thể xảy ra nếu session vẫn còn nhưng token chưa được đặt vào
                    // hoặc đã bị xóa bởi một logic nào đó (ví dụ logout) mà session chưa invalidate
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token không tìm thấy trong phiên làm việc. Vui lòng đăng nhập lại.");
                    return;
                }

                if (this.jwtTokenUtil.validateTokenAgainstSession(tokenFromHeader, userDetails, jwtFromSession)) {
                    if (SecurityContextHolder.getContext().getAuthentication() == null ||
                            !SecurityContextHolder.getContext().getAuthentication().getName().equals(userDetails.getUsername())) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails.getUsername(), // Sử dụng UserDetails làm principal
                                null,
                                userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        log.debug("Successfully authenticated user: {} for request: {}", emailFromToken, request.getServletPath());
                    }

                } else {
                    // Lý do validateTokenAgainstSession trả về false có thể là:
                    // 1. Token từ header đã hết hạn (đã được check một phần khi extractEmail)
                    // 2. Email trong token không khớp UserDetails (ít khả năng nếu đã loadUserByUsername)
                    // 3. Quan trọng nhất: tokenFromHeader không khớp jwtFromSession
                    log.warn("Token validation failed against session for user: {}. Header token might be old or session token is different. Path: {}", emailFromToken, request.getServletPath());
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ hoặc phiên đăng nhập đã thay đổi. Vui lòng đăng nhập lại.");
                    return;
                }
            } else if (emailFromToken != null && SecurityContextHolder.getContext().getAuthentication() != null) {
                log.debug("User {} already authenticated for request: {}. Proceeding.", emailFromToken, request.getServletPath());
            }
            // Nếu emailFromToken là null, đã được xử lý bởi catch block ở trên (MalformedJwtException, etc.)

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Đây là catch-all cuối cùng cho các lỗi không mong muốn
            // Nếu các exception cụ thể hơn đã được bắt và xử lý (return), nó sẽ không đến đây.
            log.error("Unexpected error in JwtTokenFilter for {}: {}", request.getServletPath(), e.getMessage(), e);
            if (!response.isCommitted()) {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã có lỗi xảy ra phía máy chủ.");
            }
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        // Dọn dẹp context trước khi gửi lỗi, đảm bảo an toàn
        SecurityContextHolder.clearContext();
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8"); // Quan trọng charset
        // Ví dụ JSON response đơn giản
        String errorJson = String.format("{\"timestamp\":%d, \"status\":%d, \"error\":\"%s\", \"message\":\"%s\"}",
                System.currentTimeMillis(),
                status,
                (status == HttpServletResponse.SC_UNAUTHORIZED ? "Unauthorized" : "Error"),
                message);
        response.getWriter().write(errorJson);
        response.getWriter().flush();
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        String requestURI = request.getServletPath();
        String method = request.getMethod();

        // Bỏ qua cho Swagger
        if (requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") || // Thường là /v3/api-docs không có / ở cuối
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/webjars")) {
            return true;
        }

        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of("/access/login", "POST"),
                Pair.of("/access/forgotPassword", "POST")
                // Thêm các endpoint khác cần bypass ở đây
        );

        for (Pair<String, String> bypassToken : bypassTokens) {
            if (bypassToken.getFirst().equals(requestURI) && bypassToken.getSecond().equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
}
