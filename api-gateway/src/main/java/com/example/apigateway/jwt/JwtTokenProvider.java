package com.example.apigateway.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey secretKey;

    @PostConstruct //이 어노테이션 있을 시 함수 자동 호출 (1화성 초기화)
    public void init() {
        // 최초 1회 생성 시 자동 호출
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }
    // 토근 생성 -> 기본 재료는 이메일
    public String createToken(String email){

        Claims claims = Jwts.claims().setSubject(email);

        claims.put("email", email);
        // 추가정보 있으면 추가

        return Jwts.builder()
                //추가 정보
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token){

        try{
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    // 주어진 토큰으로 이메일 흭득
    public String getEmailFromToken(String token){
        try {
            // 정보를 뽑을 때 오류가 나면 (만료 시간이 넘었을 시 또한 오류) false
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("email", String.class);

        }catch (ExpiredJwtException e){ // 만료시간 검사
            System.out.println("Expired JWT token");
            throw e;
        }catch(Exception e){ // 유효성 검사
           System.out.println("JWT token could not be parsed");
           throw e;
        }
    }
}

