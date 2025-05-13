package com.example.computerweb.components;

import com.example.computerweb.customexceptions.InvalidParamException;
import com.example.computerweb.models.entity.AccountEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.IAccountRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
//    @Value("${jwt.expiration}")
//    private int expiration;
//    @Value("${jwt.secretKey}")
//    private String secretKey;
//
//    private final IAccountRepository iAccountRepository;
//
//    public String generateToken(UserEntity user) throws Exception {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("email" , user.getAccountEntity().getEmail());
//        try {
//            String token = Jwts.builder()
//                    .setClaims(claims)
//                    .setSubject(user.getAccountEntity().getEmail())
//                    .setExpiration( new Date(System.currentTimeMillis() + expiration * 1000L))
//                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                    .compact();
//            return token;
//        }catch (Exception e){
//            throw new InvalidParamException("Cannot create jwt token, error: "+e.getMessage());
//        }
//
//
//    }
//
//    private Key getSignInKey() {
//        byte[] bytes = Decoders.BASE64.decode(secretKey);
//        //Keys.hmacShaKeyFor(Decoders.BASE64.decode("TaqlmGv1iEDMRiFp/pHuID1+T84IABfuA0xXh4GhiUI="));
//        return Keys.hmacShaKeyFor(bytes);
//    }
//
//    public Claims extractAllClaims ( String token ){
//
//        return Jwts.parserBuilder()
//                .setSigningKey(getSignInKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        Claims claims = this.extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    public String extractEmailFromSubjectToken (String token){
//        return extractClaim(token, Claims::getSubject );
//    }
//
//    public boolean isTokenExpired ( String token ){
//
//        return extractClaim(token, Claims::getExpiration).before(new Date());
//    }
//
//    public boolean validateToken ( String token , UserDetails userDetails , String jwtFromSession){
//
//        String emailOfToken = extractClaim(token , Claims::getSubject);
//        if (jwtFromSession == null) {
//            return false; // Hoặc throw một exception cụ thể nếu muốn
//        }
//            if (emailOfToken.equals(userDetails.getUsername()) && !isTokenExpired(token) && token.trim().equals(jwtFromSession.trim())  )
//            {
//                return true;
//            }
//
//
//       return false;
//    }

    @Value("${jwt.expiration}")
    private int expiration; // Giữ lại kiểu int nếu bạn muốn, nhưng Long thường dùng cho time
    @Value("${jwt.secretKey}")
    private String secretKey;



    public String generateToken(UserEntity user) throws InvalidParamException { // Bỏ throws Exception nếu không cần thiết nữa
        Map<String, Object> claims = new HashMap<>();
        // Nên đặt thêm thông tin định danh user ID nếu có, không chỉ email
        // claims.put("userId", user.getId());
        claims.put("email", user.getAccountEntity().getEmail());
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getAccountEntity().getEmail()) // Subject thường là username/email
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L)) // Đảm bảo expiration là long
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            // Log lỗi ở đây nếu cần thiết
            throw new InvalidParamException("Không thể tạo JWT token, lỗi: " + e.getMessage());
        }
    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaims(String token) {
        // Các exception này sẽ được bắt ở filter nếu cần
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractEmailFromSubject(String token) { // Đổi tên để rõ ràng hơn là email từ subject
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (ExpiredJwtException e) { // Có thể token đã hết hạn ngay lúc parse
            return true;
        }
    }

    /**
     * Xác thực token dựa trên UserDetails và token từ session.
     *
     * @param token          JWT từ header
     * @param userDetails    Thông tin người dùng từ UserDetailsService
     * @param jwtFromSession JWT từ HttpSession
     * @return true nếu token hợp lệ và khớp, false ngược lại
     */
    public boolean validateTokenAgainstSession(String token, UserDetails userDetails, String jwtFromSession) {
        // 1. Kiểm tra jwtFromSession có null không (nên được kiểm tra trước khi gọi hàm này ở filter)
        if (jwtFromSession == null) {
            System.err.println("JwtTokenUtil.validateTokenAgainstSession: jwtFromSession is null. This should be checked before calling.");
            return false;
        }

        try {
            final String emailFromToken = extractEmailFromSubject(token);
            // 2. Kiểm tra email khớp và token chưa hết hạn
            if (!emailFromToken.equals(userDetails.getUsername()) || isTokenExpired(token)) {
                return false;
            }
            // 3. Kiểm tra token từ header có giống hệt token trong session không
            boolean check = token.trim().equals(jwtFromSession.trim());
            return check;

        } catch (SignatureException e) {
            System.err.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false; // Nếu có bất kỳ exception nào khi parse token hoặc kiểm tra
    }
}
