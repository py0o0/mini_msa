package com.example.msasbuser.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // JWT 시크릿키 처리 -> 필요할 때 호출해서 사용 ->
    private Key getSecretKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createAccessToken(String email, String role){
        return createToken(email,role, accessTokenExpiration);
    }

    public String createRefreshToken(){
        return createToken(null,null,refreshTokenExpiration);
    }

    // 토근 생성 -> 기본 재료는 이메일
    public String createToken(String email, String role, long expiration){

        Map<String, Object> claims = new HashMap<>();

        if(email != null)
            claims.put("email", email);

        if(role != null)
            claims.put("role", role);

        Date now = new Date();
        Date expriyDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                //추가 정보
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expriyDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token){

        try{
            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }

//    // 주어진 토큰으로 이메일 흭득
//    public String getEmailFromToken(String token){
//        try {
//            // 정보를 뽑을 때 오류가 나면 (만료 시간이 넘었을 시 또한 오류) false
//            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            return claims.get("email", String.class);
//
//        }catch (ExpiredJwtException e){ // 만료시간 검사
//            System.out.println("Expired JWT token");
//            throw e;
//        }catch(Exception e){ // 유효성 검사
//           System.out.println("JWT token could not be parsed");
//           throw e;
//        }
//    }
}

