package com.example.computerweb.components;

import com.example.computerweb.customexceptions.InvalidParamException;
import com.example.computerweb.models.entity.AccountEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.IAccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
    @Value("${jwt.expiration}")
    private int expiration;
    @Value("${jwt.secretKey}")
    private String secretKey;

    private final IAccountRepository iAccountRepository;

    public String generateToken(UserEntity user) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email" , user.getAccountEntity().getEmail());
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getAccountEntity().getEmail())
                    .setExpiration( new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        }catch (Exception e){
            throw new InvalidParamException("Cannot create jwt token, error: "+e.getMessage());
        }


    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        //Keys.hmacShaKeyFor(Decoders.BASE64.decode("TaqlmGv1iEDMRiFp/pHuID1+T84IABfuA0xXh4GhiUI="));
        return Keys.hmacShaKeyFor(bytes);
    }

    public Claims extractAllClaims ( String token ){

        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractEmailToken (String token){
        return extractClaim(token, Claims::getSubject );
    }

    public boolean isTokenExpired ( String token ){

        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public boolean validateToken ( String token , UserDetails userDetails){

        String emailOfToken = extractClaim(token , Claims::getSubject);
        AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(userDetails.getUsername()).get();
        String tokenDb = account.getToken();
        if ( tokenDb != null && !tokenDb.isEmpty()){
            if (emailOfToken.equals(userDetails.getUsername()) && !isTokenExpired(token) && token.trim().equals(tokenDb.trim())  )
            {
                return true;
            }
        }

       return false;
    }

}
